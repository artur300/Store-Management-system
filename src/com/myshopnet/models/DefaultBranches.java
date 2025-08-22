package com.myshopnet.models;

import com.myshopnet.repository.BranchRepository;

public final class DefaultBranches {
    public static final String TLV = "TLV";
    public static final String JRS = "JRS";
    public static final String HFA = "HFA";

    private DefaultBranches() {}

    /** רושם את שלושת הסניפים הקבועים לתוך ה-Directory */
    public static void createAll(BranchRepository dir) {
        dir.create(new Branch(TLV, "Tel-Aviv"));
        dir.create(new Branch(JRS, "Jerusalem"));
        dir.create(new Branch(HFA, "Haifa"));
    }
}
