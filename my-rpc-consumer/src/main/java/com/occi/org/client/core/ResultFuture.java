package com.occi.org.client.core;

import com.occi.org.client.param.ClientRequest;
import com.occi.org.client.param.Response;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @author: occi
 * @date: 2024/10/1
 */
@Data
public class ResultFuture {

    public static ConcurrentHashMap<Long, ResultFuture> allResultFuture = new ConcurrentHashMap<>();

    final Lock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();

    private Response response;

    private Long timeOut = 2 * 60 * 1000L;

    private Long start = System.currentTimeMillis();

    final Logger logger = LoggerFactory.getLogger(ResultFuture.class);
    public ResultFuture(ClientRequest request) {
        allResultFuture.put(request.getId(), this);
    }

    public static void receive(Response response) {
        ResultFuture resultFuture = allResultFuture.get(response.getId());
        if (resultFuture != null) {
            Lock lock = resultFuture.lock;
            lock.lock();
            try {
                resultFuture.set(response);
                resultFuture.condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    private void set(Response response) {
        this.response = response;
    }

    /**
     * 主线程调用get方法获取结果，阻塞等待，等待时释放锁
     * @return
     */
    public Response get() {
        lock.lock();
        try {
            while (notDone()) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return this.response;

    }

    public Response get(Long time){
        lock.lock();
        try {
            while(notDone()){
                condition.await(time, TimeUnit.MILLISECONDS);
                if((System.currentTimeMillis()-start) > time){
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("get response error", e);
        } finally {
            lock.unlock();
        }
        return this.response;

    }

    private boolean notDone() {
        return this.response == null;
    }

    /**
     * 清理超时的线程
     */
    static class ClearFutureThread extends Thread{
        @Override
        public void run() {
            Set<Long> ids = allResultFuture.keySet();
            for(Long id : ids){
                ResultFuture f = allResultFuture.get(id);
                if (f == null){
                    allResultFuture.remove(id);
                } else if(f.getTimeOut() < (System.currentTimeMillis() - f.getStart())) {
                    //链路超时，设置超时的结果
                    Response res = new Response();
                    res.setId(id);
                    res.setCode("33333");
                    res.setMsg("链路超时");
                    receive(res);
                }
            }
        }
    }

    static {
        ClearFutureThread clearThread = new ClearFutureThread();
        clearThread.setDaemon(true);
        clearThread.start();
    }

}
