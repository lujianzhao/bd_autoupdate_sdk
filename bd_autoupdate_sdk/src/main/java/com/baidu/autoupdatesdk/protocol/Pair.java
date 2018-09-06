//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.protocol;

public class Pair<F, S> {
    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Pair)) {
            return false;
        } else {
            Pair other;
            try {
                other = (Pair)o;
            } catch (ClassCastException var4) {
                return false;
            }

            return this.first.equals(other.first) && this.second.equals(other.second);
        }
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + this.first.hashCode();
        result = 31 * result + this.second.hashCode();
        return result;
    }

    public static <A, B> Pair<A, B> create(A a, B b) {
        return new Pair(a, b);
    }
}
