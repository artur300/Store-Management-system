package com.myshopnet.branches;

public final class DefaultBranches {
    public static final String TLV = "TLV";
    public static final String JRS = "JRS";
    public static final String HFA = "HFA";

    private DefaultBranches() {}

    /** רושם את שלושת הסניפים הקבועים לתוך ה-Directory */
    public static void registerAll(BranchDirectory dir) {
        dir.register(new Branch(TLV, "Tel-Aviv"));
        dir.register(new Branch(JRS, "Jerusalem"));
        dir.register(new Branch(HFA, "Haifa"));
    }
}
