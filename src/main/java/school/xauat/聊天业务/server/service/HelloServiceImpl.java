package school.xauat.聊天业务.server.service;

/**
 * @author ：zsy
 * @date ：Created 2021/12/5 20:38
 * @description：
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String helloService(String name) {
        int a = 1 / 0;
        return "Hello " + name + " !" ;
    }
}
