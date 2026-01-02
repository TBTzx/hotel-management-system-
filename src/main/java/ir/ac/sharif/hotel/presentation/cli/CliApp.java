package ir.ac.sharif.hotel.presentation.cli;

import ir.ac.sharif.hotel.app.Bootstrap;
import ir.ac.sharif.hotel.application.dto.OccupantDto;
import ir.ac.sharif.hotel.application.dto.RoomDetailsDto;
import ir.ac.sharif.hotel.application.dto.RoomSummaryDto;
import ir.ac.sharif.hotel.application.service.*;
import ir.ac.sharif.hotel.domain.model.finance.DiscountPolicy;
import ir.ac.sharif.hotel.domain.model.guest.Guest;
import ir.ac.sharif.hotel.domain.model.reservation.PenaltyPolicy;
import ir.ac.sharif.hotel.domain.model.reservation.Reservation;
import ir.ac.sharif.hotel.domain.model.room.RoomStatus;
import ir.ac.sharif.hotel.domain.model.room.RoomType;
import ir.ac.sharif.hotel.domain.model.service.ServiceType;
import ir.ac.sharif.hotel.domain.model.service.TimeSlot;
import ir.ac.sharif.hotel.domain.model.user.Credentials;
import ir.ac.sharif.hotel.domain.model.user.Role;
import ir.ac.sharif.hotel.domain.model.user.User;
import ir.ac.sharif.hotel.infrastructure.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class CliApp {
    private final Scanner sc = new Scanner(System.in);

    private final UserRepository userRepo = new InMemoryUserRepository();
    private final RoomRepository roomRepo = new InMemoryRoomRepository();
    private final ReservationRepository resRepo = new InMemoryReservationRepository();
    private final ServiceBookingRepository sbRepo = new InMemoryServiceBookingRepository();
    private final PaymentRepository payRepo = new InMemoryPaymentRepository();

    private final AuthService auth = new AuthService(userRepo);
    private final UserService users = new UserService(userRepo);
    private final RoomService rooms = new RoomService(roomRepo);
    private final ReservationService reservations = new ReservationService(roomRepo, resRepo, sbRepo, PenaltyPolicy.defaultPolicy());
    private final GuestService guests = new GuestService(resRepo);
    private final ExtraServiceService extras = new ExtraServiceService(resRepo, sbRepo);
    private final BillingService billing = new BillingService(resRepo, roomRepo, sbRepo, DiscountPolicy.loyaltyUpTo40());
    private final PaymentService payment = new PaymentService(resRepo, payRepo);
    private final HotelQueryService hotelQuery = new HotelQueryService(resRepo);

    private User current;

    public void run() {
        Bootstrap.ensureMainAdmin(userRepo);
        seedRooms();

        while (true) {
            if (current == null) authMenu();
            else mainMenu();
        }
    }

    private void seedRooms() {
        User root = userRepo.findByUsername("root").orElseThrow();
        rooms.addRoom(root, RoomType.STANDARD, 101, 1, 1);
        rooms.addRoom(root, RoomType.SUITE, 201, 2, 2);
        rooms.addRoom(root, RoomType.DELUXE, 301, 2, 3);
        rooms.addRoom(root, RoomType.PRESIDENTIAL, 401, 3, 4);
    }

    private void authMenu() {
        System.out.println("\n=== AUTH ===");
        System.out.println("1) Register");
        System.out.println("2) Login");
        System.out.println("0) Exit");
        int c = readInt();
        try {
            if (c == 1) register();
            else if (c == 2) login();
            else if (c == 0) System.exit(0);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void mainMenu() {
        System.out.println("\n=== MAIN (" + current.getUsername() + " / " + current.getRole() + ") ===");
        System.out.println("1) List rooms (summary)");
        System.out.println("2) Room details");
        System.out.println("3) Reserve + Pay");
        System.out.println("4) Cancel reservation");
        System.out.println("5) My reservations history");
        System.out.println("6) Add guest to reservation");
        System.out.println("7) Book extra service");
        System.out.println("8) Invoice for reservation");
        System.out.println("9) Logout");

        if (current.getRole() == Role.ADMIN_MAIN) {
            System.out.println("20) List users");
            System.out.println("21) Set user role");
        }
        if (current.getRole() == Role.ADMIN_MAIN || current.getRole() == Role.ADMIN_RESERVATION) {
            System.out.println("30) Current occupants");
            System.out.println("31) Reservations history by username");
            System.out.println("32) All reservations");
        }
        if (current.getRole() == Role.ADMIN_MAIN || current.getRole() == Role.ADMIN_SERVICES) {
            System.out.println("40) Change room status");
        }

        int c = readInt();
        try {
            switch (c) {
                case 1 -> listRooms();
                case 2 -> roomDetails();
                case 3 -> reserveAndPay();
                case 4 -> cancelReservation();
                case 5 -> myHistory();
                case 6 -> addGuest();
                case 7 -> bookService();
                case 8 -> showInvoice();
                case 9 -> current = null;

                case 20 -> listUsers();
                case 21 -> setRole();

                case 30 -> occupants();
                case 31 -> historyByUsername();
                case 32 -> allReservations();

                case 40 -> changeRoomStatus();
                default -> System.out.println("Unknown option");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void register() {
        System.out.print("username: "); String u = sc.nextLine().trim();
        System.out.print("password: "); String p = sc.nextLine().trim();
        auth.register(new Credentials(u, p));
        System.out.println("Registered.");
    }

    private void login() {
        System.out.print("username: "); String u = sc.nextLine().trim();
        System.out.print("password: "); String p = sc.nextLine().trim();
        current = auth.login(new Credentials(u, p));
        System.out.println("Logged in.");
    }

    private void listRooms() {
        System.out.print("type (STANDARD/SUITE/DELUXE/PRESIDENTIAL or empty): ");
        String t = sc.nextLine().trim();
        RoomType type = t.isEmpty() ? null : RoomType.valueOf(t);
        System.out.print("floor (number or empty): ");
        String f = sc.nextLine().trim();
        Integer floor = f.isEmpty() ? null : Integer.parseInt(f);

        List<RoomSummaryDto> list = rooms.listRooms(type, floor);
        for (var r : list) System.out.println("- id=" + r.id() + " beds=" + r.beds());
    }

    private void roomDetails() {
        System.out.print("room id: ");
        int id = readInt();
        RoomDetailsDto d = rooms.getRoomDetails(id);
        System.out.println(d);
    }

    private void reserveAndPay() {
        System.out.print("room ids (comma separated): ");
        List<Integer> ids = parseIntList(sc.nextLine());
        System.out.print("start date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(sc.nextLine().trim());
        System.out.print("end date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(sc.nextLine().trim());

        int past = billing.pastReservationsCountForUser(current.getUsername());
        Reservation r = reservations.reserveRooms(current, ids, start, end);
        var inv = billing.calculateInvoice(r.getId(), past);

        System.out.println("Final payable=" + inv.getFinalPayable() + " (discountRate=" + inv.getDiscountRate() + ")");
        System.out.print("pay amount: ");
        long amount = Long.parseLong(sc.nextLine().trim());

        payment.pay(r.getId(), inv, amount);
        System.out.println("Reserved & paid. reservationId=" + r.getId());
    }

    private void cancelReservation() {
        System.out.print("reservation id: ");
        String id = sc.nextLine().trim();
        long penalty = reservations.cancelReservation(current, id);
        System.out.println("Canceled. penalty=" + penalty);
    }

    private void myHistory() {
        List<Reservation> list = reservations.historyForUser(current);
        for (var r : list) System.out.println("- " + r.getId() + " rooms=" + r.getRoomIds() + " paid=" + r.isPaid());
    }

    private void addGuest() {
        System.out.print("reservation id: ");
        String id = sc.nextLine().trim();
        System.out.print("guest full name: ");
        String name = sc.nextLine().trim();
        System.out.print("guest nationalId: ");
        String nid = sc.nextLine().trim();
        guests.addGuest(id, new Guest(name, nid));
        System.out.println("Guest added.");
    }

    private void bookService() {
        System.out.print("reservation id: ");
        String id = sc.nextLine().trim();
        System.out.print("service type (POOL/GYM/CONFERENCE_HALL): ");
        ServiceType type = ServiceType.valueOf(sc.nextLine().trim());
        System.out.print("start datetime (YYYY-MM-DDTHH:MM): ");
        LocalDateTime s = LocalDateTime.parse(sc.nextLine().trim());
        System.out.print("end datetime (YYYY-MM-DDTHH:MM): ");
        LocalDateTime e = LocalDateTime.parse(sc.nextLine().trim());
        extras.book(current, id, type, new TimeSlot(s, e));
        System.out.println("Booked.");
    }

    private void showInvoice() {
        System.out.print("reservation id: ");
        String id = sc.nextLine().trim();
        int past = billing.pastReservationsCountForUser(current.getUsername());
        var inv = billing.calculateInvoice(id, past);
        System.out.println("Room costs: " + inv.getRoomCosts());
        System.out.println("Services cost: " + inv.getServicesCost());
        System.out.println("Discount rate: " + inv.getDiscountRate());
        System.out.println("Final payable: " + inv.getFinalPayable());
    }

    private void listUsers() {
        var list = users.listUsers(current);
        for (var u : list) System.out.println("- " + u.getId() + " " + u.getUsername() + " " + u.getRole());
    }

    private void setRole() {
        System.out.print("target user id: ");
        String id = sc.nextLine().trim();
        System.out.print("role (USER/ADMIN_RESERVATION/ADMIN_SERVICES/ADMIN_MAIN): ");
        Role role = Role.valueOf(sc.nextLine().trim());
        users.setRole(current, id, role);
        System.out.println("Role updated.");
    }

    private void occupants() {
        List<OccupantDto> list = hotelQuery.listCurrentOccupants(current);
        for (var o : list) System.out.println("- " + o.username() + " room=" + o.roomId());
    }

    private void historyByUsername() {
        System.out.print("username: ");
        String u = sc.nextLine().trim();
        List<Reservation> list = reservations.historyByUsername(current, u);
        for (var r : list) System.out.println("- " + r.getId() + " rooms=" + r.getRoomIds() + " paid=" + r.isPaid());
    }

    private void allReservations() {
        List<Reservation> list = reservations.allReservations(current);
        for (var r : list) System.out.println("- " + r.getId() + " user=" + r.getUsername() + " paid=" + r.isPaid());
    }

    private void changeRoomStatus() {
        System.out.print("room id: ");
        int roomId = readInt();
        System.out.print("status (AVAILABLE/OCCUPIED/CLEANING): ");
        RoomStatus st = RoomStatus.valueOf(sc.nextLine().trim());
        rooms.changeRoomStatus(current, roomId, st);
        System.out.println("Changed.");
    }

    private int readInt() {
        String s = sc.nextLine().trim();
        return Integer.parseInt(s);
    }

    private static List<Integer> parseIntList(String s) {
        String[] parts = s.split(",");
        List<Integer> out = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(Integer.parseInt(t));
        }
        return out;
    }
}
