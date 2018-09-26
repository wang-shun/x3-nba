package com.ftkj.x3.client;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainT {
    public static void main(String[] args) {
        int sum = 0;
        for (int i = 0; i < 10000; i++) {
            sum += i;
            sum += (i - 1);
        }
        System.out.println(sum);

        Stream.of("one", "two", "three", "four")
            .filter(e -> e.length() > 3)
            .peek(e -> System.out.println("Filtered value: " + e))
            .map(String::toUpperCase)
            .peek(e -> System.out.println("Mapped value: " + e))
            .collect(Collectors.toList());
    }

    /**
     * 7.2 Move Field（搬移字段）.
     */
    public static class MoveField {
        /**
         * 我想把表示利率的_interestRate搬移到AccountType类去.
         */
        static class Account {
            private AccountType accountType1;

            double interestForAmount_days(double amount, int days) {
                return accountType1.getInterestRate() * amount * days / 365;
            }

            public double getInterestRate() {
                return accountType1.getInterestRate();
            }

            public void setInterestRate(double _interestRate) {
                accountType1.setInterestRate(_interestRate);
            }

            public static class AccountType1 {

            }
        }

        static class AccountType {
            private double _interestRate;

            public double getInterestRate() {
                return _interestRate;
            }

            public void setInterestRate(double _interestRate) {
                this._interestRate = _interestRate;
            }
        }
    }
}
