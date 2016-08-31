package com.s515.rpc.router.lbs;

import com.s515.rpc.router.LoadBalance;
import com.s515.rpc.router.server.Node;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 8/31/2016.
 */
public class RandomLoadBalance implements LoadBalance {
    private final Random random = new Random();

    @Override
    public Node select(List<Node> nodes) {
        if (nodes == null || nodes.size() == 0)
            return null;

        if (nodes.size() == 1)
            return nodes.get(0);

        return doSelect(nodes);
    }

    private Node doSelect(List<Node> nodes) {
        int size = nodes.size();
        int totalWeight = 0;

        boolean sameWeight = true;

        for (int i = 0; i < size; i++) {
            totalWeight += nodes.get(i).getWeight();

            if (sameWeight && i > 0 && nodes.get(i).getWeight() != nodes.get(i - 1).getWeight())
                sameWeight = false;
        }

        if (!sameWeight && totalWeight > 0) {
            int offset = random.nextInt(totalWeight);

            for (int i = 0; i < size; i++) {
                offset = offset - nodes.get(i).getWeight();

                if (offset < 0)
                    return nodes.get(i);
            }
        }

        // if node weight is same
        return nodes.get(random.nextInt(size));
    }
}
