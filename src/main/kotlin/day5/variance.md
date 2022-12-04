# 변성
제네릭형의 대체가능성
기본타입에서 부모/자식 타입간의 대체를 파라미터 타입에서 하고 싶은 것 
이게 없으면 메서드들을 다 오버로딩해줘야함 (귀찮음)

### 무공변 : 타입 끼리 아무 관계가 없다.
```kotlin
val v: Tree<Number> = Tree<Int> // 이게 됐으면 좋겠다
```
### 공변 : 파라미터 타입에서 기본타입의 대체가능성을 따라하는 경우
  - 제네릭이 더 많은 타입을 유연하게 받을 수 있음
  - T가 노출되지 않으면 무엇인지 모르고 그냥 T 로서 만 다루어지는데 아래처럼 밖으로 노출되면 Number 로 사용하기때문에 기본타입의 rule(대체가능성) 을 따라줘야함  
```kotlin
class Tree<out T>(val value: T)
val tree: Tree<Number> = Tree<Int>(10)
tree.value.toDouble() 
```
- Number를 기대하니까 실제 구현체인 Int, Long 뭐를 가져와도 상관이없어서 `출력으로만 사용 가능`
- T가 인자로노출되면 실제 구현체는 Int인데 Number처럼 사용함으로 Long같은 것이 들어올수 있어서 `받아들이는 것은 안됌`
- 그래서 out 키워드를 달아서 읽기전용이라고 확신을주고 공변으로 쓸 수 있는 것

### 반공변 : 반대로 파라미터 타입에 추상타입이 들어오는 경우
```kotlin
class Node<in T:Number>(
  private val value: T, // 외부에 출력되지 않음 가시성때문에 
  private val next: Node<T>? = null
){
     operator fun contains(target: T): Boolean {
        return if(value.toInt() == target.toInt()) true else next.contains(target) ?: false
    }
}
val node: Node<Int> = Node<Number>(8.0)
node.contains(8)
```
- 인자로써 T가 들어올때 반공변이 가능함, Int는 어차피 그 상위타입의 모든 메서드를 가지고 있기때문에 contains에서 상위타입의 메서드를 쓸수 있음 `받아들이는 것 가능` 
- upper bound 는 T를 활용하기 위한 보조이지 반공변과는 상관이없다.  
- T가 노출되면 Int 를 기대하는데 구현체는 Number이기 때문에 Long 같은 것들이 튀어나올수 있어 `출력으로는 사용 불가능`
- in 을 붙혀서 받기만 하는것이라 보장하고 반공변으로 쓰는 것

### 선언 시점 변성
java에는 없는 기능 class 선언 시점에 in,out 써서 변셩을 지정해주는 것
### 사용 시점 변성
함수 시그니쳐에 in,out을 지정해 주는 것
다음 강의에 더 다룸