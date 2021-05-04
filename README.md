# ActLogger

## 프로그램 설명

> 해당 어플리케이션은 스마트폰의 위치 정보 센서를 통해 위치정보를 받아온 뒤, 그 위치정보에 따른 활동정보에 따라 자동으로 시간표에 기록해주는 어플리케이션입니다.  
활동정보는 사용자가 위치와 범위에 따라 설정할 수 있으며 현재 위치가 설정된 범위 안에 있으면 활동을 기록하는 형식입니다.  
또한 활동정보는 색상을 지정할 수 있으며 해당 색상으로 시간표에 기록되고, 하루동안의 통계정보도 제공하여 자신이 어떤 활동을 얼만큼 했는지 한눈에 보기 쉽게 하였습니다.  
위치 정보는 어플리케이션이 꺼져 있어도 항상 받아와야 하므로 10분마다 위치 정보를 받아와 데이터베이스에 기록하여 주는 Foreground Service 로 구성하였으며, UI인 Activity는 시간표, 통계, 설정 3가지의 Fragment로 나누어 구성하였습니다.

## UI
![image01](https://user-images.githubusercontent.com/8454866/116961694-ffd81300-acde-11eb-9a64-f8188a320e20.png)
![image02](https://user-images.githubusercontent.com/8454866/116961696-01094000-acdf-11eb-85ca-40fd2f870078.png)
![image03](https://user-images.githubusercontent.com/8454866/116961699-01a1d680-acdf-11eb-8c20-c38171fb42a7.png)
![image04](https://user-images.githubusercontent.com/8454866/116961703-02d30380-acdf-11eb-942d-229e50994340.png)
