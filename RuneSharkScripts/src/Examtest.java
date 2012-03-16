import api.methods.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/4/12
 * Time: 6:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Examtest {

    public static void main(String args[]) {
        try {
            Integer.parseInt("HI");
        }  catch(Throwable e) {
            String[] a = Arrays.toString(e.getStackTrace()).split(",");
            for (String z : a) {
                System.out.println(z);
            }
//            System.out.print(Arrays.toString(e.getStackTrace()));
        }
    }


    
}
