package com.eaton.platform.core.constants;

public enum DownloadOption {
    DOWNLOAD("download"),
    EMAIL("email");

    private String downloadOption;

    private DownloadOption(final String downloadOption)	{
        this.downloadOption = downloadOption;
    }

    public String getDownloadOption() {
        return downloadOption;
    }

    public static DownloadOption fromDownloadOption(final String downloadOption) {
        for (final DownloadOption option : DownloadOption.values()) {
            if (option.downloadOption == downloadOption) {
                return option;
            }
        }
        throw new IllegalArgumentException(downloadOption);
    }
}