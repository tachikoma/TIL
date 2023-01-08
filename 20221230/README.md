파이썬으로 이전 배열의 개수가 몇 개든 300개만 추출하는 처리하도록 코드 작성 해봄

처음과 끝은 항상 포함되어야 함

실제로는 상황에 따라 최대 301 개가 됨

```
list = []
size = 2000
step = size/300
print(f"step = {step}")
for i in range(0, 300):
    list.append(round(i * step))
print('list = %d' % len(list))
print('unique = %d' % len(set(list)))
print('size = %d' % size)
print('last index = %d' % list[300-1])
isNeedExtra = list[300-1] != size-1
print(f'need end = {isNeedExtra}')
```

[코드](test300.py)