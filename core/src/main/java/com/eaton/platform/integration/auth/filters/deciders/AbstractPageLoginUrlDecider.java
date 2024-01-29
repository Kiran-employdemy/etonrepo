package com.eaton.platform.integration.auth.filters.deciders;

public abstract class AbstractPageLoginUrlDecider implements Comparable<AbstractPageLoginUrlDecider> {

    protected int orderRanking;

    public abstract boolean conditionMatched();
    public abstract String redirectTo();

    public int compareTo(AbstractPageLoginUrlDecider compareTo) {
        if(this.orderRanking > compareTo.getRankingOrder()){
            return 1;
        }else if(this.orderRanking < compareTo.getRankingOrder()){
            return -1;
        }else{
            return 0;
        }
    }

    public int getRankingOrder(){
        return orderRanking;
    }

}
