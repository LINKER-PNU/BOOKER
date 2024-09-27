# 3장. 함수

## 작게 만들어라!

```java
// 목록 3-3 HtmlUtil.java (re-refactored)
// Good code
public static String renderPageWithSetupsAndTeardowns(
    PageData pageData, boolean isSuite) throws Exception{
        if (isTestPage(pageData))
            includeSetupAndTeardownPages(pageData, isSuite);
        return pageData.getHtml();
    }
```

- 함수는 2~4줄이 제일 이상적.
- `if` `else` `while`문의 블록에는 한 줄만.
- 함수의 들여쓰기 수준은 1~2단을 넘어서면 안된다.

## 한 가지만 해라!

- 함수는 한 가지만 수행해야한다.
- 여기서의 한 가지는 추상화 수준이 하나인 단계를 뜻함.
- 의미있게 다른 함수를 추출할 수 있다면 그 함수는 여러가지를 하는 것임.

## 위에서 아래로 코드 읽기: 내려가기 규칙

- 위에서 아래로 이야기처럼 읽혀야 가독성이 뛰어나다.
- 내려 읽으면서 추상화 단계가 한 단계씩 낮아져야한다.

## Switch 문

- Switch 문은 본래 N가지를 처리한다.
``` java
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
```
- 문제점
    - 긴 길이의 함수.
    - 직원에 따라 여러가지의 작업을 수행.
    - SRP/OCP 위반.
    - 필요하다면 `isPayday` `deliverPay`같은 함수를 일일이 만들어야함.
        - 이러한 함수들은 또 똑같이 유해한 구조를 지님.


- 다형성(Polymorphism)을 통해 극복가능.
- Switch 문을 추상 팩토리에 숨김.
- 팩토리에서는 switch로 instance 생성.
- 함수는 `Employee` 추상클래스를 거쳐 호출.

``` java
// 목록 3-5 Employee and Factory
// Good code
public abstract class Employee{
    public abstract boolean isPayday();
    public abstract Money calculatePay();
    public abstract void deliverPay(Money pay);
}
// -----------------------------
public interface EmployeeFactory{
    public Employee makeEmployee(EmployeeRecord r) throws InvalidEmployeeType;
}
// -----------------------------
public class EmployeeFactoryImpl implements EmployeeFactory{
    public Employee makeEmployee(EmployeeRecord r) throws InvalidEmployeeType{
        switch(r.type){
            case COMMISSIONED:
                return new CommissionedEmployee(r);
            case HOURLY:
                return new HourlyEmployee(r);
            case SALARIED:
                return new SalariedEmployee(r);
            default:
                throw new InvalidEmployeeType(r.type);
        }
    }
}
```
- 이러한 구조라도 가급적 자제할 것.
- 그러나 실전에서는 위반할 일도 많을 것이다.

## 서술적인 이름을 사용하라!
- 좋은 예) `SetupTeardownIncluder` `includeSetupAndTeardownPages` 
- 이미 지어진 이름이라면 IDE의 refactor 기능으로 한 번에 바꾸자.

## 함수 인수
- 제일 이상적인 인수는 0항.
- 3항부터는 가급적 회피.
- 많은 인수는 함수를 이해하기 힘들게 만듦.
    - 테스트 케이스 또한 복잡해짐.
- 출력인수는 사용하지 말 것.
    - 입력 - 인수 / 출력 - 반환 값
    - 출력인수 : 참조로 입력되는 인수.

- `new Point(0,0)`과 같이 인수가 다수인것이 자연스러운 경우는 예외.

## 오류 코드보다 예외를 사용하라, Try/Catch 블록 뽑아내기
- 오류 코드 예) `if (deletePage(page) == E_OK)`
- 호출자는 이를 곧바로 처리해야 하는 문제 직면.
- 여러단계로 중첩되는 코드를 야기.

- `try` 블록 내의 동작을 별도 함수로 뽑아내라.
``` java
// page 58
// Good code
public void delete(Page page){
    try{
        deletePageAndAllReferences();
    }
    catch (Exception e) {
        logError(e);
    }
}

private void deletePageAndAllReferences(Page page) throws Exception {
    deletePage(page);
    registry.deleteReference(page.name);
    configKeys.deleteKey(page.name.makeKey());
}

private void logError(Exception e) {
    logger.log(e.getMessage());
}
```
- 오류처리도 한 가지 작업이다.
    - 함수가 `try로` 시작하면 `catch`/`finally`로 끝나야한다.

## 반복하지 마라!
- `SetUp` `SuiteSetup` 등 하나의 알고리즘이 여러번 반복될 수 있다.
- 알고리즘이 변하면 여러 곳을 고쳐야하는 문제점 발생.
- `include`로 중복제거

## 구조적 프로그래밍
- Edsger Dijkstra의 구조적 프로그래밍 원칙
- 큰 함수에서의 규율
- 모든 함수, 블록은 입구와 출구가 하나씩만 존재하여야한다.
- `return`은 하나만.
- `break` `continue` `goto`는 금지.

- 그러나 작은 함수라면 별 의미없다.
    - `return` `break` `continue`가 다수라도 문제없음.
    - `goto`는 큰 함수에서만 의미가 있으므로 여전히 기피.

