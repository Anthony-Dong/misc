package com.chat.conf;

import com.chat.conf.model.NServerInfo;
import org.junit.Test;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * TODO
 *
 * @date:2019/11/13 11:46
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class TestPriorityQueue {


    @Test
    public void test() throws InterruptedException {


        NServerInfo nServerInfo = new NServerInfo();
        nServerInfo.setTotalConnection(10);


        NServerInfo nServerInfo2 = new NServerInfo();
        nServerInfo2.setTotalConnection(15);

        NServerInfo nServerInfo3 = new NServerInfo();
        nServerInfo3.setTotalConnection(20);


        NServerInfo nServerInfo4 = new NServerInfo();
        nServerInfo4.setTotalConnection(25);




        PriorityBlockingQueue<NServerInfo> queue = new PriorityBlockingQueue<NServerInfo>(10, new Comparator<NServerInfo>() {
            @Override
            public int compare(NServerInfo o1, NServerInfo o2) {
                return Integer.compare(o1.getTotalConnection(), o2.getTotalConnection());
            }
        });


        queue.offer(nServerInfo);
        queue.offer(nServerInfo2);
        queue.offer(nServerInfo3);
        queue.offer(nServerInfo4);

        queue.forEach(e->{
            System.out.println(e);
        });

        System.out.println("=============================");

        System.out.println("queue.take() = " + queue.take());

    }

}
