package com.danieltwc.aws.glacier.commands;

public abstract class VaultCommand extends GlacierCommand {
    protected String vaultName;

    public void setVaultName(String vaultName) {
        this.vaultName = vaultName;
    }

    public void run() throws Exception {
        if (args.size() < 1) {
            throw new IllegalArgumentException("No vault name specified");
        }

        vaultName = args.remove(0);
        this.setVaultName(vaultName);
    }
}
