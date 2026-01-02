package ir.ac.sharif.hotel.app;

import ir.ac.sharif.hotel.application.service.*;
import ir.ac.sharif.hotel.domain.model.finance.DiscountPolicy;
import ir.ac.sharif.hotel.domain.model.reservation.PenaltyPolicy;
import ir.ac.sharif.hotel.domain.model.room.RoomType;
import ir.ac.sharif.hotel.infrastructure.repository.*;
import ir.ac.sharif.hotel.infrastructure.persistence.BackupManager;

import java.nio.file.Files;
import java.nio.file.Path;

public final class AppContext {
    public final UserRepository userRepo = new InMemoryUserRepository();
    public final RoomRepository roomRepo = new InMemoryRoomRepository();
    public final ReservationRepository resRepo = new InMemoryReservationRepository();
    public final ServiceBookingRepository sbRepo = new InMemoryServiceBookingRepository();
    public final PaymentRepository payRepo = new InMemoryPaymentRepository();

    public final AuthService auth = new AuthService(userRepo);
    public final UserService users = new UserService(userRepo);
    public final RoomService rooms = new RoomService(roomRepo);
    public final ReservationService reservations =
            new ReservationService(roomRepo, resRepo, sbRepo, PenaltyPolicy.defaultPolicy());
    public final GuestService guests = new GuestService(resRepo);
    public final ExtraServiceService extras = new ExtraServiceService(resRepo, sbRepo);
    public final BillingService billing =
            new BillingService(resRepo, roomRepo, sbRepo, DiscountPolicy.loyaltyUpTo40());
    public final PaymentService payment = new PaymentService(resRepo, payRepo);
    public final HotelQueryService hotelQuery = new HotelQueryService(resRepo);

    
private static final AppContext INSTANCE = new AppContext();

private final BackupManager backupManager = new BackupManager();
private final Path backupFile = Path.of("backup", "hotel_backup.dat");

private AppContext() {
    boolean restored = tryRestore();

    if (!restored) {
        Bootstrap.ensureDefaultAdmins(userRepo);
        seedRooms();
    } else {
        if (userRepo.findAll().isEmpty()) {
            Bootstrap.ensureDefaultAdmins(userRepo);
        }
        if (roomRepo.findAll().isEmpty()) {
            seedRooms();
        }
    }

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        try {
            backupManager.save(backupFile, userRepo, roomRepo, resRepo, sbRepo, payRepo);
        } catch (Exception e) {
            System.err.println("Backup failed: " + e.getMessage());
        }
    }));
}

private boolean tryRestore() {
    try {
        if (!Files.exists(backupFile)) return false;

        var snap = backupManager.load(backupFile);

        ((InMemoryUserRepository) userRepo).clearAndLoad(snap.getUsers());
        ((InMemoryRoomRepository) roomRepo).clearAndLoad(snap.getRooms());
        ((InMemoryReservationRepository) resRepo).clearAndLoad(snap.getReservations());
        ((InMemoryServiceBookingRepository) sbRepo).clearAndLoad(snap.getServiceBookings());
        ((InMemoryPaymentRepository) payRepo).clearAndLoad(snap.getPayments());

        return true;
    } catch (Exception e) {
        System.err.println("Restore failed, starting fresh: " + e.getMessage());
        return false;
    }
}
    private void seedRooms() {
        var root = userRepo.findByUsername("root").orElseThrow();
        rooms.addRoom(root, RoomType.STANDARD, 101, 1, 1);
        rooms.addRoom(root, RoomType.SUITE, 201, 2, 2);
        rooms.addRoom(root, RoomType.DELUXE, 301, 2, 3);
        rooms.addRoom(root, RoomType.PRESIDENTIAL, 401, 3, 4);
    }

    public static AppContext get() { return INSTANCE; }
}
