package com.myshopnet.models.branches;

import com.myshopnet.repository.BranchRepository;

public final class DefaultBranches {
    public static final String TLV = "TLV";
    public static final String JRS = "JRS";
    public static final String HFA = "HFA";

    private DefaultBranches() {}

    /** רושם את שלושת הסניפים הקבועים לתוך ה-Directory */
    public static void registerAll(BranchRepository dir) {
        dir.register(new Branch(TLV, "Tel-Aviv"));
        dir.register(new Branch(JRS, "Jerusalem"));
        dir.register(new Branch(HFA, "Haifa"));
    }
}
