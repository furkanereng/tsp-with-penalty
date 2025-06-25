# Traveling Salesman Problem with Penalty (TSPwP)
In this variant of the Traveling Salesman Problem (TSP), each city carries a penalty if it is not  visited and it is asked that how can we minimize the value of the length of the tour plus the  penalties for the not visited cities. This program finds an approximate solution for the defined TSPwP problem. This is the second project assignment for Analysis of Algorithms course.

# How to Run?
- This program requires 4 input files each named as *test-input-X.txt* where X is a number from 1 to 4.
- You can directly run the program after compiling once you have downloaded all of the source code files and generated the input files with the given format.
- The format of the input files must be as below:
```
<Fixed Penalty Value: Integer>
<CityID: 0> <X Coordinate: Integer> <Y Coordinate: Integer>
<CityID: 1> <X Coordinate: Integer> <Y Coordinate: Integer>
<CityID: 2> <X Coordinate: Integer> <Y Coordinate: Integer>
...
```
- The output format will be as below:
```
<Total Cost of Tour> <Number of Visited Cities>
<ID of First Visited City>
<ID of Second Visited City>
<ID of Third Visited City>
...
```
