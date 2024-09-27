// 목록 3-4 Payroll.java
// Bad code
public Money calculatePay(Employ e)
throws InvalidEmployeeType{
    switch(e.type){
        case COMMISSIONED:
            return calculateCommissionedPay(e);
        case HOURLY:
            return calculateHourlyPay(e);
        case SALARIED:
            return calculateSalariedDPay(e);
        default:
            throw new InvalidEmployeeeType(e.type);
    }
}