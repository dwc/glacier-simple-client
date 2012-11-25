package com.danieltwc.aws.glacier.commands.impl;

import java.util.List;

import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;

import com.danieltwc.aws.glacier.commands.GlacierCommand;

public class ListCommand extends GlacierCommand {
    public void run() {
        String marker = null;

        // Take an optional marker to list more than 1000 vaults
        if (args.size() > 0) {
            marker = args.get(0);
        }

        out.println("Listing vaults...");

        ListVaultsRequest request = new ListVaultsRequest()
            .withAccountId("-");
        if (marker != null) {
            request.withMarker(marker);
        }

        ListVaultsResult result = client.listVaults(request);
        displayVaultList(result);

        String msg = "Finished listing vaults";

        String nextMarker = result.getMarker();
        if (nextMarker != null) {
            msg += "; continue list using using marker [" + nextMarker + "]";
        }

        out.println(msg);
    }

    private void displayVaultList(ListVaultsResult result) {
        List<DescribeVaultOutput> vaults = result.getVaultList();

        for (DescribeVaultOutput vault : vaults) {
            displayVault(vault);
        }
    }

    private void displayVault(DescribeVaultOutput vault) {
        StringBuffer buf = new StringBuffer(vault.getVaultName());
        buf.append("\t");

        buf.append(vault.getVaultARN());
        buf.append("\t");

        buf.append(vault.getCreationDate());
        buf.append("\t");

        buf.append(vault.getLastInventoryDate());
        buf.append("\t");

        buf.append(vault.getNumberOfArchives());
        buf.append("\t");

        buf.append(vault.getSizeInBytes());
        buf.append("\t");

        out.println(buf);
    }
}
