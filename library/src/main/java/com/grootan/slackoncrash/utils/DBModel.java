package com.grootan.slackoncrash.utils;

import java.util.Random;
import java.util.UUID;

/**
 * Created by lokeshravichandru on 22/10/17.
 */

public class DBModel {
    private String id;
    private String hook;
    private String request;

    public DBModel(String id, String hook,String request)
    {
        if(id == null) {
            this.id = UUID.randomUUID().toString();
        }
        else
        {
            this.id = id;
        }
        this.hook= hook;
        this.request = request;
    }

    public String getRequest()
    {
        return this.request;
    }

    public String getHook()
    {
        return this.hook;
    }

    public String getId()
    {
        return this.id;
    }


    public Object[] getArray() {
        return new Object[]{
                id,hook,request
        };
    }

    public String[] columnNames() {
        return new String[]{
               "id", "hook","request"
        };
    }
}
