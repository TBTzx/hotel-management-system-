package ir.ac.sharif.hotel.infrastructure.persistence;

import ir.ac.sharif.hotel.infrastructure.repository.PaymentRepository;
import ir.ac.sharif.hotel.infrastructure.repository.ReservationRepository;
import ir.ac.sharif.hotel.infrastructure.repository.RoomRepository;
import ir.ac.sharif.hotel.infrastructure.repository.ServiceBookingRepository;
import ir.ac.sharif.hotel.infrastructure.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


public class BackupManager {

    public void save(Path file,
                     UserRepository userRepo,
                     RoomRepository roomRepo,
                     ReservationRepository resRepo,
                     ServiceBookingRepository sbRepo,
                     PaymentRepository payRepo) throws IOException {

        Files.createDirectories(file.getParent());

        BackupSnapshot snapshot = new BackupSnapshot(
                userRepo.findAll(),
                roomRepo.findAll(),
                resRepo.findAll(),
                sbRepo.findAll(),
                payRepo.findAll()
        );

        try (OutputStream os = Files.newOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(snapshot);
        }
    }

    public BackupSnapshot load(Path file) throws IOException, ClassNotFoundException {
        try (InputStream is = Files.newInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(is)) {
            Object obj = ois.readObject();
            return (BackupSnapshot) obj;
        }
    }
}
