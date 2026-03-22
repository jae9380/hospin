# html_report 생성
```
jmeter -g /Users/jae/results/폴더명/파일명.jtl -o /Users/jae/results/폴더명/html_report
jmeter -g D:\Jmeter\hospin\results\폴더명\파일명.jtl -o D:\Jmeter\hospin\results\폴더명\html_report
```

# Windows

## CSV Data Set Config

```
C:/Users/ljy53/IdeaProjects/hospin/jmeter/data/test_accounts.samole.csv
```

## Simple Data Writer

```
D:/Jmeter/hospin/results/a_${__time(YMDHMS)}/scenario_a_${__time(YMDHMS)}.jtl

D:/Jmeter/hospin/results/b_${__time(YMDHMS)}/scenario_b_stress_${__time(YMDHMS)}.jtl

D:/Jmeter/hospin/results/b_${__time(YMDHMS)}/scenario_b_concentrated_${__time(YMDHMS)}.jtl

D:/Jmeter/hospin/results/c_${__time(YMDHMS)}/scenario_c_${__time(YMDHMS)}.jtl

D:/Jmeter/hospin/results/d_${__time(YMDHMS)}/scenario_d_${__time(YMDHMS)}.jtl
```


---

# Mac

## CSV Data Set Config

```
/Users/jae/Desktop/jmeter/test_accounts.csv
```

## Simple Data Writer

```
/Users/jae/results/a_${__time(YMDHMS)}/scenario_a_${__time(YMDHMS)}.jtl

/Users/jae/results/b_${__time(YMDHMS)}/scenario_b_stress_${__time(YMDHMS)}.jtl

/Users/jae/results/b_${__time(YMDHMS)}/scenario_b_concentrated_${__time(YMDHMS)}.jtl

/Users/jae/results/c_${__time(YMDHMS)}/scenario_c_${__time(YMDHMS)}.jtl

/Users/jae/results/d_${__time(YMDHMS)}/scenario_d_${__time(YMDHMS)}.jtl
```