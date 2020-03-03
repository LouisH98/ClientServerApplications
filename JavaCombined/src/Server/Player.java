package Server;

import java.io.PrintWriter;
import java.util.Scanner;

/*
This class holds all the information on each player
 */

public class Player {
    private final int ID;
    private boolean hasBall;

    private PrintWriter writer;
    private Scanner scanner;

    public Player(int id, boolean hasBall, Scanner scanner, PrintWriter writer) {
        this.ID = id;
        this.hasBall = hasBall;

        this.writer = writer;
        this.scanner = scanner;
    }

    public int getID() {
        return ID;
    }

    public boolean hasBall() {
        return hasBall;
    }

    public void setHasBall(boolean hasBall) {
        this.hasBall = hasBall;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Player) obj).getID() == getID();
    }
}
