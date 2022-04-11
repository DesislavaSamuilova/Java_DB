import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "minions_db";
    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static Connection connection;

    public static void main(String[] args) throws IOException, SQLException {

        connection = getConnection();

        System.out.println("Enter exercise number:");
        int exerciseNum = Integer.parseInt(reader.readLine());

        switch (exerciseNum) {
            case 2 -> exerciseTwo();
            case 3 -> exerciseThree();
            case 4 -> exerciseFour();
            case 5 -> exerciseFive();
            case 6 -> exerciseSix();
            case 7 -> exerciseSeven();
            case 8 -> exerciseEight();
            case 9 -> exerciseNine();
            default -> throw new IllegalStateException("Unexpected value: " + exerciseNum);
        }


    }

    private static void exerciseSix() throws IOException, SQLException {
        System.out.println("Enter villain id: ");
        int villainId = Integer.parseInt(reader.readLine());

        PreparedStatement selectVillain = connection.prepareStatement(
                "SELECT name FROM villains WHERE id = ?");
        selectVillain.setInt(1, villainId);
        ResultSet villainSet = selectVillain.executeQuery();

        if (!villainSet.next()) {
            System.out.println("No such villain was found");
            return;
        }

        String villainName = villainSet.getString("name");

        PreparedStatement selectAllVillainMinions = connection.prepareStatement(
                "SELECT COUNT(DISTINCT minion_id) as m_count" +
                        " FROM minions_villains WHERE villain_id = ?");
        selectAllVillainMinions.setInt(1, villainId);
        ResultSet minionsCountSet = selectAllVillainMinions.executeQuery();
        minionsCountSet.next();
        int countMinionsDeleted = minionsCountSet.getInt("m_count");

        connection.setAutoCommit(false);

        try {
            PreparedStatement deleteMinionsVillains = connection.prepareStatement(
                    "DELETE FROM minions_villains WHERE villain_id = ?");
            deleteMinionsVillains.setInt(1, villainId);
            deleteMinionsVillains.executeUpdate();

            PreparedStatement deleteVillain = connection.prepareStatement(
                    "DELETE FROM villains WHERE id = ?");
            deleteVillain.setInt(1, villainId);
            deleteVillain.executeUpdate();

            connection.commit();

            System.out.println(villainName + " was deleted");
            System.out.println(countMinionsDeleted + " minions released");
        } catch (SQLException e) {
            e.printStackTrace();

            connection.rollback();
        }

        connection.close();
    }

    private static void exerciseNine() throws IOException, SQLException {
        System.out.println("Enter minion id: ");
        int minionId = Integer.parseInt(reader.readLine());

        CallableStatement callableStatement = connection.prepareCall
                ("CALL usp_get_older(?)");
        callableStatement.setInt(1, minionId);
        callableStatement.executeUpdate();

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT name,age FROM minions");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            System.out.printf("%s %d %n", resultSet.getString("name"),
                    resultSet.getInt("age"));
        }
    }

    private static void exerciseEight() throws SQLException, IOException {
        System.out.println("Enter:");
        List<String> ids = List.of(reader.readLine().split("\\s+"));
        String listOfIds = String.join(", ", ids);
        String sql = String.format("UPDATE minions SET age = age + 1, name = lower(name) WHERE id IN (%s);", listOfIds);

        PreparedStatement updateTable = connection.prepareStatement(sql);
        updateTable.executeUpdate();

        PreparedStatement getMinions = connection.prepareStatement("SELECT name, age FROM minions;");
        ResultSet resultSet = getMinions.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString(1);
            int age = resultSet.getInt(2);

            System.out.printf("%s %d%n", name, age);
        }
        connection.close();
    }

    private static void exerciseSeven() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT name FROM minions");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> allMinionsNames = new ArrayList<>();
        while (resultSet.next()) {
            allMinionsNames.add(resultSet.getString(1));
        }
        int start = 0;
        int end = allMinionsNames.size() - 1;
        for (int i = 0; i < allMinionsNames.size(); i++) {
            System.out.println(i % 2 == 0
                    ? allMinionsNames.get(start++)
                    : allMinionsNames.get(end--));
        }
    }

    private static void exerciseFive() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String countryName = scanner.nextLine();

        PreparedStatement updateTownNames = connection.prepareStatement(
                "UPDATE towns SET name = UPPER(name) WHERE country = ?;"
        );
        updateTownNames.setString(1, countryName);

        int updatedCount = updateTownNames.executeUpdate();

        if (updatedCount == 0) {
            System.out.println("No town names were affected.");
            return;
        }
        System.out.println(updatedCount + " town names were affected.");
        PreparedStatement selectAllTowns = connection.prepareStatement(
                "SELECT name FROM towns WHERE country = ?"
        );
        selectAllTowns.setString(1, countryName);
        ResultSet townsSet = selectAllTowns.executeQuery();

        List<String> towns = new ArrayList<>();

        while (townsSet.next()) {
            String townName = townsSet.getString("name");
            towns.add(townName);
        }
        System.out.println(towns);
        connection.close();
    }

    private static void exerciseFour() throws IOException, SQLException {
        Scanner scanner = new Scanner(System.in);
        String[] minionInfo = scanner.nextLine().split(" ");
        String minionName = minionInfo[1];
        int minionAge = Integer.parseInt(minionInfo[2]);
        String minionTown = minionInfo[3];

        String villainName = scanner.nextLine().split(" ")[1];

        int townId = getOrInsertTown(connection, minionTown);
        int villainId = getOrInsertVillain(connection, villainName);

        PreparedStatement insertMinion = connection.prepareStatement(
                "INSERT INTO minions(name, age, town_id) VALUES(?, ?, ?)");
        insertMinion.setString(1, minionName);
        insertMinion.setInt(2, minionAge);
        insertMinion.setInt(3, townId);
        insertMinion.executeUpdate();

        PreparedStatement getLastMinion = connection.prepareStatement(
                "SELECT id FROM minions ORDER BY id DESC LIMIT 1");
        ResultSet lastMinionSet = getLastMinion.executeQuery();
        lastMinionSet.next();
        int lastMinionId = lastMinionSet.getInt("id");

        PreparedStatement insertMinionsVillains = connection.prepareStatement(
                "INSERT INTO minions_villains VALUES (?, ?)"
        );
        insertMinionsVillains.setInt(1, lastMinionId);
        insertMinionsVillains.setInt(2, villainId);
        insertMinionsVillains.executeUpdate();

        System.out.printf("Successfully added %s to be minion of %s.%n",
                minionName, villainName);
    }

    private static int getOrInsertVillain(Connection connection, String villainName) throws SQLException {
        PreparedStatement selectVillain = connection.prepareStatement(
                "SELECT id FROM villains WHERE name = ?");
        selectVillain.setString(1, villainName);

        ResultSet villainSet = selectVillain.executeQuery();

        int villainId = 0;
        if (!villainSet.next()) {
            PreparedStatement insertVillain = connection.prepareStatement(
                    "INSERT INTO villains(name, evilness_factor) VALUES(?, ?)");
            insertVillain.setString(1, villainName);
            insertVillain.setString(2, "evil");

            insertVillain.executeUpdate();

            ResultSet newVillainSet = selectVillain.executeQuery();
            newVillainSet.next();
            villainId = newVillainSet.getInt("id");
            System.out.printf("Villain %s was added to the database.%n", villainName);
        } else {
            villainId = villainSet.getInt("id");
        }

        return villainId;
    }

    private static int getOrInsertTown(Connection connection, String minionTown) throws SQLException {
        PreparedStatement selectTown = connection.prepareStatement(
                "SELECT id FROM towns WHERE name = ?");
        selectTown.setString(1, minionTown);

        ResultSet townSet = selectTown.executeQuery();

        int townId = 0;
        if (!townSet.next()) {
            PreparedStatement insertTown = connection.prepareStatement(
                    "INSERT INTO towns(name) VALUES (?);");
            insertTown.setString(1, minionTown);
            insertTown.executeUpdate();

            ResultSet newTownSet = selectTown.executeQuery();
            newTownSet.next();
            townId = newTownSet.getInt("id");
            System.out.printf("Town %s was added to the database.%n", minionTown);
        } else {
            townId = townSet.getInt("id");
        }

        return townId;
    }

    private static void exerciseThree() throws IOException, SQLException {
        System.out.println("Enter villain id:");
        int villainId = Integer.parseInt(reader.readLine());
        getAllMinionsByVillainId(villainId);
    }

    private static void getAllMinionsByVillainId(int villainId) throws SQLException {
        PreparedStatement villainStatement = connection.prepareStatement(
                "SELECT name FROM villains WHERE id = ?;");
        villainStatement.setInt(1, villainId);
        ResultSet villainSet = villainStatement.executeQuery();

        if (!villainSet.next()) {
            System.out.printf("No villain with ID %d exists in the database.", villainId);
            return;
        }
        String villainName = villainSet.getString("name");
        System.out.println("Villains: " + villainName);

        PreparedStatement minionStatement = connection.prepareStatement(
                "SELECT m.name, m.age FROM minions m " +
                        "JOIN minions_villains mv on m.id = mv.minion_id " +
                        "WHERE mv.villain_id = ?;");
        minionStatement.setInt(1, villainId);

        ResultSet minionSet = minionStatement.executeQuery();
        for (int i = 1; minionSet.next(); i++) {
            String name = minionSet.getString("name");
            int age = minionSet.getInt("age");
            System.out.printf("%d. %s %d%n", i, name, age);
        }
    }

    private static void exerciseTwo() throws SQLException {
        PreparedStatement prepareStatement = connection.prepareStatement
                ("SELECT v.name, COUNT(DISTINCT mv.minion_id) as `m_count` FROM villains as v " +
                        "JOIN minions_villains as mv ON mv.villain_id = v.id" +
                        " GROUP BY v.name" +
                        " HAVING `m_count` > ?;");
        prepareStatement.setInt(1, 15);


        ResultSet resultSet = prepareStatement.executeQuery();
        while (resultSet.next()) {
            System.out.printf("%s %s%n", resultSet.getString
                            (1),
                    resultSet.getString(2));
        }

    }

    public static Connection getConnection() throws IOException, SQLException {
        System.out.print("Enter user:");
         String user = reader.readLine();
         System.out.print("Enter password:");
          String password = reader.readLine();

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);

        return DriverManager
                .getConnection(CONNECTION_STRING + DB_NAME, properties);

    }

}



