package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Created on 13.01.2020 15:38.
 *
 * @author Aleks Sidorenko (e-mail: alek.sidorenko@gmail.com).
 * @version Id$.
 * @since 0.1.
 */
public class Services {

    private DbProperties props = new DbProperties();
    private Connection conn;

    public void init() {
        Scanner sc = new Scanner(System.in);

        try {
            try {
                // create connection
                conn = DriverManager.getConnection(props.getUrl(), props.getUser(), props.getPassword());
                initDB();

                while (true) {
                    System.out.println("1: add apartment");
                    System.out.println("2: delete apartment");
                    System.out.println("3: change apartment");
                    System.out.println("4: choice of apartments for the maximum area");
                    System.out.println("5: choice of apartments for the maximum price");
                    System.out.println("6: view apartments");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addApartment(sc);
                            break;
                        case "2":
                            deleteApartment(sc);
                            break;
                        case "3":
                            changeApartment(sc);
                            break;
                        case "4":
                            selectionOfApartmentsByArea(sc);
                            break;
                        case "5":
                            selectionOfApartmentsByPrice(sc);
                            break;
                        case "6":
                            viewApartments();
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                if (conn != null) conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    public void initDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS Apartments");
            st.execute("CREATE TABLE Apartments (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, district VARCHAR(50) NOT NULL, address VARCHAR(50) NOT NULL, area DOUBLE NOT NULL, number_of_rooms INT, price DOUBLE )");
        } finally {
            st.close();
        }
    }

    public void addApartment(Scanner sc) throws SQLException {
        System.out.print("Enter district of apartment: ");
        String district = sc.nextLine();
        System.out.print("Enter address of apartment: ");
        String address = sc.nextLine();
        System.out.print("Enter area of apartment: ");
        String sArea = sc.nextLine();
        double area = Double.parseDouble(sArea);
        System.out.print("Enter number of rooms: ");
        String sNumb = sc.nextLine();
        int number_of_rooms = Integer.parseInt(sNumb);
        System.out.print("Enter price of apartment: ");
        String sPrise = sc.nextLine();
        double price = Double.parseDouble(sPrise);

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Apartments (district, address, area, number_of_rooms, price) VALUES(?, ?, ?, ?, ?)");
        try {
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setDouble(3, area);
            ps.setInt(4, number_of_rooms);
            ps.setDouble(5, price);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
    }

    public void deleteApartment(Scanner sc) throws SQLException {
        System.out.print("Enter id apartment: ");
        String sId = sc.nextLine();
        int id = Integer.parseInt(sId);

        PreparedStatement ps = conn.prepareStatement("DELETE FROM Apartments WHERE id = ?");
        try {
            ps.setInt(1, id);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
    }

    public void changeApartment(Scanner sc) throws SQLException {
        System.out.print("Enter apartment id: ");
        String sId = sc.nextLine();
        int id = Integer.parseInt(sId);
        System.out.print("Enter district of apartment: ");
        String district = sc.nextLine();
        System.out.print("Enter address of apartment: ");
        String address = sc.nextLine();
        System.out.print("Enter area of apartment: ");
        String sArea = sc.nextLine();
        double area = Double.parseDouble(sArea);
        System.out.print("Enter number of rooms: ");
        String sNumb = sc.nextLine();
        int number_of_rooms = Integer.parseInt(sNumb);
        System.out.print("Enter price of apartment: ");
        String sPrise = sc.nextLine();
        double price = Double.parseDouble(sPrise);

        PreparedStatement ps = conn.prepareStatement("UPDATE Apartments SET district = ?, address = ?, area = ?, number_of_rooms = ?, price = ? WHERE id = ?");
        try {
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setDouble(3, area);
            ps.setInt(4, number_of_rooms);
            ps.setDouble(5, price);
            ps.setInt(6, id);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }

    }

    public void selectionOfApartmentsByArea(Scanner sc) throws SQLException {
        System.out.print("Enter maximum area of apartment: ");
        String sArea = sc.nextLine();
        double area = Double.parseDouble(sArea);
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE area <= ?");
        try {
            ps.setDouble(1, area);
            // table of data representing a database result set,
            ResultSet rs = ps.executeQuery();
            view(rs);
        } finally {
            ps.close();
        }
    }

    public void selectionOfApartmentsByPrice(Scanner sc) throws SQLException {
        System.out.print("Enter maximum price of apartment: ");
        String sPrise = sc.nextLine();
        double price = Double.parseDouble(sPrise);
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE price <= ?");
        try {
            ps.setDouble(1, price);
            // table of data representing a database result set,
            ResultSet rs = ps.executeQuery();
            view(rs);
        } finally {
            ps.close();
        }
    }

    public void viewApartments() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments");
        try {
            // table of data representing a database result set,
            ResultSet rs = ps.executeQuery();
            view(rs);
        } finally {
            ps.close();
        }
    }

    private void view(ResultSet rs) throws SQLException {
        try {
            // can be used to get information about the types and properties of the columns in a ResultSet object
            ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++)
                System.out.print(md.getColumnName(i) + "\t\t");
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t\t");
                }
                System.out.println();
            }
        } finally {
            rs.close(); // rs can't be null according to the docs
        }
    }
}
