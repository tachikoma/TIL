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
