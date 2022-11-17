package com.zhangyun.filecloud.server;

import com.zhangyun.filecloud.common.entity.FileTransferBO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/16 15:48
 * @since: 1.0
 */
public class CommonTest {
    @Test
    public void testNull() {
        Long a = null;
        long b = a;
        System.out.println(b);
    }

    @Test
    public void testFilter() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(4);
        list.add(3);
        list.add(2);
        List<Integer> list1 = list.stream().filter(i -> i.equals(1)).collect(Collectors.toList());
        System.out.println(list1);
    }
}
