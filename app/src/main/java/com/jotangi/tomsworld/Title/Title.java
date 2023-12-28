package com.jotangi.tomsworld.Title;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Title {
    public TitleInterface titleInterface;

    public void setBarTitle(String title)
    {
        set(title);
    }
    private void set(final String str) {
        final ExecutorService service = Executors.newSingleThreadExecutor();

        service.submit(new Runnable() {
            @Override
            public void run() {
                titleInterface.getString(str);
            }
        });
    }
}
