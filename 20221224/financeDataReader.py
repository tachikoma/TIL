import FinanceDataReader as fdr
import pandas as pd
import matplotlib.pyplot as plt

#삼성전자
sdf = fdr.DataReader('005930', '20030101')
print(sdf.head())
sdf['Close'].plot(figsize=(12,6), grid=True)
plt.show()