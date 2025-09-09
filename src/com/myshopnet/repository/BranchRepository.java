package com.myshopnet.repository;

import com.myshopnet.data.Data;
import com.myshopnet.models.Branch;
import java.util.List;

public class BranchRepository implements Repository<Branch> {
    @Override
    public Branch create(Branch branch) {
        Data.getBranches().put(branch.getId(), branch);

        return Data.getBranches().get(branch.getId());
    }

    @Override
    public Branch update(String id, Branch branch) {
        Branch branchToReturn = null;

        if (Data.getBranches().containsKey(id)) {
            branchToReturn = Data.getBranches().put(id, branch);
        }

        return branch;
    }

    @Override
    public void delete(String id) {
        Data.getBranches().remove(id);
    }

    @Override
    public Branch get(String id) {
        return Data.getBranches().get(id);
    }

    @Override
    public List<Branch> getAll() {
        return Data.getBranches().values().stream().toList();
    }
}

