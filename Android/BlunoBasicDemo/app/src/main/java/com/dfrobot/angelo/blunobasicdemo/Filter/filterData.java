package com.dfrobot.angelo.blunobasicdemo.Filter;

import java.util.*;

public class filterData {
    private static filter f1;
    private static filter f2;
    private static filter f3;

    public int calculateRespRate( ArrayList<Float> data) {
        int initsize = 16;  //TODO: CHANGE THIS!
        boolean grtThanFormer, grtThanLatter;
        int numBreaths = 0;

        f1 = new filter(initsize);
        f2 = new filter(initsize);
        f3 = new filter(initsize);
        List<Float> y = new ArrayList<Float>();

        for (Float f : data) {
            y.add(f3.step(f2.step(f1.step(f))));
        }

        for (int i = 0; i < (f1.size() + f2.size() + f3.size()); i++) { //remove filter setup values (worthless)
            //y.remove(i);
            y.remove(0);    //TODO: IS THIS RIGHT? keep removing first element since array shifts after each removal
        }
         //TODO: CATCH INDEX OUT OF BOUNDS EXCEPTIONS (SET MINIMUM)!!!
        for (int i = 1; i < y.size() - 1; i++) { //does not check first and last value
            grtThanFormer = y.get(i - 1) < y.get(i);
            grtThanLatter = y.get(i + 1) < y.get(i);
            if (grtThanFormer && grtThanLatter) {
                numBreaths++;
            }
        }

        return numBreaths;
    }

}
