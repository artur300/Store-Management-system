package com.myshopnet.service;

import com.myshopnet.auth.PasswordPolicy;
import com.myshopnet.auth.SessionRegistry;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.logs.LoggerImpl;
import com.myshopnet.models.Role;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * שירות אימות בסיסי:
 * - רישום חשבונות (עם מדיניות סיסמה)
 * - לוגין/לוגאאוט (כולל מניעת כניסה כפולה דרך SessionRegistry)
 * - עזרי הרשאות לפי תפקיד ולפי סניף
 */
public class AuthService {

    private final Map<String, UserAccount> accounts = new ConcurrentHashMap<>();
    private final SessionRegistry sessions = new SessionRegistry();
    private final PasswordPolicy policy = new PasswordPolicy();

    /** רישום חשבון חדש (ללא בדיקת תפקיד; משתמשים בזה מאחורי הקלעים או דרך registerByAdmin) */
    public void register(UserAccount acc) {
        if (!policy.isValid(acc.getPassword()))
            throw new IllegalArgumentException("Weak password");
        if (accounts.putIfAbsent(acc.getUsername(), acc) != null)
            throw new IllegalArgumentException("Username already exists");
        LoggerImpl.getInstance().log(new LogEvent(
                LogType.EMPLOYEE_REGISTERED,
                "user=" + acc.getUsername() + ", role=" + acc.getRole() + ", branch=" + acc.getBranchId()));
    }

    /** לוגין: יוצר session ומחזיר token; חוסם כניסה כפולה */
    public String login(String username, String password) {
        UserAccount acc = accounts.get(username);
        if (acc == null || !acc.getPassword().equals(password))
            throw new SecurityException("Bad credentials");
        SessionRegistry.SessionInfo s = sessions.create(username);
        LoggerImpl.getInstance().log(new LogEvent(LogType.LOGIN, "user=" + username));
        return s.getToken();
    }

    /** לוגאאוט לפי token */
    public void logout(String token) {
        SessionRegistry.SessionInfo s = sessions.get(token);
        if (s != null) {
            sessions.end(token);
            LoggerImpl.getInstance().log(new LogEvent(LogType.LOGOUT, "user=" + s.getUsername()));
        }
    }

    /** בדיקת סטטוס לוגין לפי username (עוזר בעיקר לטסטים/דיאגנוסטיקה) */
    public boolean isLoggedIn(String username) { return sessions.isUserLoggedIn(username); }

    /** שליפת חשבון לפי username (לשימוש פנימי/אדמין) */
    public UserAccount getAccount(String username) { return accounts.get(username); }

    // ---------------------------
    //   עזרי הרשאות (Helpers)
    // ---------------------------

    /** 1) מפיק את החשבון מתוך token; זורק חריגה אם הסשן לא תקף */
    public UserAccount accountFromToken(String token) {
        SessionRegistry.SessionInfo s = sessions.get(token);
        if (s == null) throw new SecurityException("Invalid session");
        UserAccount acc = accounts.get(s.getUsername());
        if (acc == null) throw new IllegalStateException("Account not found");
        return acc;
    }

    /** בדיקת תפקיד (Role) בלבד; מחזיר את החשבון אם מותר, אחרת זורק SecurityException */
    public UserAccount requireRole(String token, Role... allowed) {
        UserAccount acc = accountFromToken(token);
        for (Role r : allowed) {
            if (acc.getRole() == r) return acc;
        }
        throw new SecurityException("Insufficient role");
    }

    /**
     * 2) בדיקת תפקיד וגם התאמת סניף:
     * מאשר שהמשתמש שייך לאותו branch של הפעולה, וגם שהתפקיד שלו אחד מה־allowed.
     */
    public UserAccount requireRoleInBranch(String token, String branchId, Role... allowed) {
        UserAccount acc = accountFromToken(token);
        if (!acc.getBranchId().equals(branchId)) {
            throw new SecurityException("Branch mismatch");
        }
        for (Role r : allowed) {
            if (acc.getRole() == r) return acc;
        }
        throw new SecurityException("Insufficient role");
    }

    // ---------------------------
    //   פעולות ניהול ע"י מנהל
    // ---------------------------

    /** רישום חשבון חדש ע״י מנהל משמרת (אדמין פשוט) */
    public void registerByAdmin(String adminToken, UserAccount acc) {
        requireRole(adminToken, Role.SHIFT_MANAGER);
        register(acc);
    }
}

