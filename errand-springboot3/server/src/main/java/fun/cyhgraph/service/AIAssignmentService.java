package fun.cyhgraph.service;

public interface AIAssignmentService {
    void dispatch(Integer orderId);
    void acceptAssignment(Integer orderId, Integer riderId);
    void rejectAssignment(Integer orderId);
    void checkTimeout();
}
