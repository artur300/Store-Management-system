package com.myshopnet.client;
import com.myshopnet.models.DefaultBranches;
import com.myshopnet.models.Branch;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.models.Employee;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.EmployeeService;
import com.myshopnet.models.Role;
import com.myshopnet.auth.*;

import java.util.List;
import java.util.Scanner;

public class Main {

    // --- Services & State ---
    private static final BranchRepository branches = new BranchRepository();
    private static final EmployeeService empSvc   = new EmployeeService();
    private static final AuthService auth         = new AuthService(); // לשימוש במסך "משתמש"
    private static String userToken = null; // לטובת "כניסה כמשתמש"

    // אדמין קשיח לפי דרישתך
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin";

    public static void main(String[] args) {
        DefaultBranches.registerAll(branches);
        seedSampleUser(); // כדי שאפשר יהיה להדגים כניסה כ"משתמש"

        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println("""
                        =========== START ===========
                        1) היכנס כמנהל
                        2) היכנס כמשתמש
                        0) יציאה
                        ============================
                        """);
                System.out.print("בחירה: ");
                String choice = sc.nextLine().trim();

                switch (choice) {
                    case "1" -> adminLoginAndMenu(sc);
                    case "2" -> userLoginAndMenu(sc);
                    case "0" -> running = false;
                    default -> System.out.println("בחירה לא תקינה.\n");
                }
            }
        }

        System.out.println("להתראות 👋");
    }

    // -------------------- ADMIN --------------------

    private static void adminLoginAndMenu(Scanner sc) {
        System.out.print("Admin username: ");
        String u = sc.nextLine().trim();
        System.out.print("Admin password: ");
        String p = sc.nextLine().trim();

        if (!ADMIN_USER.equals(u) || !ADMIN_PASS.equals(p)) {
            System.out.println("פרטי אדמין שגויים.\n");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("""
                    ----- Admin Menu -----
                    1) יצירת עובד (Create Employee)
                    2) עריכת עובד (Edit Employee)
                    3) רשימת עובדים לפי סניף
                    4) רשימת כל העובדים
                    0) חזרה
                    ----------------------
                    """);
            System.out.print("בחירה: ");
            String c = new Scanner(System.in).nextLine().trim();
            try {
                switch (c) {
                    case "1" -> adminCreateEmployee();
                    case "2" -> adminEditEmployee();
                    case "3" -> adminListByBranch();
                    case "4" -> adminListAll();
                    case "0" -> back = true;
                    default -> System.out.println("בחירה לא תקינה.");
                }
            } catch (Exception e) {
                System.out.println("שגיאה: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static void adminCreateEmployee() {
        Scanner sc = new Scanner(System.in);
        System.out.print("מספר עובד (ID): ");
        String id = sc.nextLine().trim();

        System.out.print("שם מלא: ");
        String fullName = sc.nextLine().trim();

        System.out.print("ת.ז: ");
        String nationalId = sc.nextLine().trim();

        System.out.print("טלפון: ");
        String phone = sc.nextLine().trim();

        System.out.print("מספר חשבון בנק: ");
        String account = sc.nextLine().trim();

        String branchId = chooseBranch(sc);
        Role role = chooseRole(sc);

        Employee e = new Employee(id, fullName, nationalId, phone, account, role, branchId);
        empSvc.add(e);
        branches.get(branchId).addEmployee(e);

        System.out.println("עובד נוצר בהצלחה.");
    }

    private static void adminEditEmployee() {
        Scanner sc = new Scanner(System.in);
        System.out.print("הכנס מספר עובד לעריכה: ");
        String id = sc.nextLine().trim();

        Employee old = empSvc.get(id);
        if (old == null) {
            System.out.println("לא נמצא עובד עם מזהה " + id);
            return;
        }

        System.out.println("השאר ריק כדי לא לשנות ערך.");

        System.out.print("שם מלא (" + old.getFullName() + "): ");
        String fullName = readOrDefault(sc, old.getFullName());

        System.out.print("ת.ז (" + old.getNationalId() + "): ");
        String nationalId = readOrDefault(sc, old.getNationalId());

        System.out.print("טלפון (" + old.getPhone() + "): ");
        String phone = readOrDefault(sc, old.getPhone());

        System.out.print("מס' חשבון (" + old.getAccountNumber() + "): ");
        String account = readOrDefault(sc, old.getAccountNumber());

        System.out.println("תפקיד נוכחי: " + old.getRole());
        Role role = chooseRole(sc, old.getRole());

        System.out.println("סניף נוכחי: " + old.getBranchId());
        String branchId = chooseBranch(sc, old.getBranchId());

        // לעדכן שיוך סניף (להסיר מהישן ולהוסיף לחדש)
        Branch oldBranch = branches.get(old.getBranchId());
        if (oldBranch != null) oldBranch.removeEmployee(old);

        Employee updated = new Employee(id, fullName, nationalId, phone, account, role, branchId);
        empSvc.upsert(updated);

        Branch newBranch = branches.get(branchId);
        if (newBranch != null) newBranch.addEmployee(updated);

        System.out.println("העובד עודכן בהצלחה.");
    }

    private static void adminListByBranch() {
        Scanner sc = new Scanner(System.in);
        String branchId = chooseBranch(sc);
        List<Employee> list = empSvc.listByBranch(branchId);
        System.out.println("עובדים בסניף " + branchId + ":");
        for (Employee e : list) {
            System.out.println(" - " + e.getId() + " | " + e.getFullName() + " | " + e.getRole());
        }
    }

    private static void adminListAll() {
        List<Employee> list = empSvc.listAll();
        System.out.println("כל העובדים:");
        for (Employee e : list) {
            System.out.println(" - " + e.getId() + " | " + e.getFullName() +
                    " | " + e.getRole() + " | סניף=" + e.getBranchId());
        }
    }

    // -------------------- USER --------------------

    private static void userLoginAndMenu(Scanner sc) {
        if (userToken != null) {
            System.out.println("כבר מחובר/ת כמשתמש. בצע/י Logout.");
            return;
        }
        System.out.print("Username: ");
        String u = sc.nextLine().trim();
        System.out.print("Password: ");
        String p = sc.nextLine().trim();

        try {
            userToken = auth.login(u, p);
        } catch (Exception e) {
            System.out.println("כניסה נכשלה: " + e.getMessage());
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("""
                    --- User Menu ---
                    1) הצג מידע משתמש
                    2) Logout
                    0) חזרה
                    -----------------
                    """);
            System.out.print("בחירה: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> showUserInfo();
                case "2" -> { auth.logout(userToken); userToken = null; System.out.println("התנתקת."); }
                case "0" -> back = true;
                default -> System.out.println("בחירה לא תקינה.");
            }
            System.out.println();
        }
    }

    private static void showUserInfo() {
        if (userToken == null) { System.out.println("לא מחובר/ת."); return; }
        UserAccount acc = auth.accountFromToken(userToken);
        System.out.println("משתמש: " + acc.getUsername() +
                " | תפקיד: " + acc.getRole() +
                " | סניף: " + acc.getBranchId() +
                " | employeeId=" + acc.getEmployeeId());
    }

    // -------------------- Helpers --------------------

    private static String readOrDefault(Scanner sc, String def) {
        String s = sc.nextLine().trim();
        return s.isEmpty() ? def : s;
    }

    private static String chooseBranch(Scanner sc) { return chooseBranch(sc, null); }
    private static String chooseBranch(Scanner sc, String current) {
        System.out.println("בחר סניף: 1) TLV  2) JRS  3) HFA" + (current != null ? "  (נוכחי: "+current+")" : ""));
        System.out.print("בחירה: ");
        String b = sc.nextLine().trim();
        return switch (b) {
            case "1" -> "TLV";
            case "2" -> "JRS";
            case "3" -> "HFA";
            default  -> (current != null ? current : "TLV");
        };
    }

    private static Role chooseRole(Scanner sc) { return chooseRole(sc, null); }
    private static Role chooseRole(Scanner sc, Role current) {
        System.out.println("בחר תפקיד: 1) SHIFT_MANAGER  2) CASHIER  3) SELLER" +
                (current != null ? "  (נוכחי: "+current+")" : ""));
        System.out.print("בחירה: ");
        String r = sc.nextLine().trim();
        return switch (r) {
            case "1" -> Role.SHIFT_MANAGER;
            case "2" -> Role.CASHIER;
            case "3" -> Role.SELLER;
            default  -> (current != null ? current : Role.SELLER);
        };
    }

    /** יוצר חשבון משתמש אחד לדוגמה כדי שתוכל לבדוק "כניסה כמשתמש" */
    private static void seedSampleUser() {
        // ניצור גם עובד וגם חשבון מערכת תואם (הסיסמה חייבת לעבור את ה-PasswordPolicy)
        Employee e = new Employee("EMP200", "Noa Cashier", "222", "050-2", "AC-2",
                Role.CASHIER, "TLV");
        empSvc.upsert(e);
        branches.get("TLV").addEmployee(e);

        // שים לב: PasswordPolicy דורש אות+ספרה ואורך >= 6, לכן לא משתמשים ב-"user/user"
        auth.register(new UserAccount("user1", "User123", "EMP200", "TLV", Role.CASHIER));
        System.out.println("משתמש דוגמה לכניסת 'משתמש': user1 / User123");
        System.out.println("כניסת אדמין: admin / admin (קשיח)");
    }
}
