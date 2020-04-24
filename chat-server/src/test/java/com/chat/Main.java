package com.chat;

import java.util.Scanner;

/**
 * TODO
 *
 * @date:2020/3/2 20:03
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Main {


    private static int makeNearestPrime(int a) {
        if (isPrime(a)) return a;
        int count = 0;
        while (true) {
            count++;
            int high = a + count;
            if (isPrime(high)) {
                return high;
            }
            int low = a - count;
            if (isPrime(low)) {
                return low;
            }
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] arr = new int[n];
        int count = n;
        while (n-- > 0) {
            int a = in.nextInt();
            arr[count - n - 1] = a;
        }

        for (int i = 0; i < arr.length; i++) {
            arr[i] = makeNearestPrime(arr[i]);
        }
        for (int i : arr) {
            System.out.println(makeNearestPrime(i));
        }
    }

    private static boolean isPrime(int n) {
        if (n <= 3) return n > 1;
        int sqrt = (int) Math.sqrt(n);
        for (int i = 2; i <= sqrt; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
