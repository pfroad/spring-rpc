package com.s515.rpc.router;

import com.s515.rpc.router.server.Node;

import java.util.List;

/**
 * Created by Administrator on 8/31/2016.
 */
public interface LoadBalance {
    Node select(List<Node> nodes);
}
