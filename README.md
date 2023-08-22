# CSE2115 - Project

## Running 
`gradle bootRun`

## Testing
```
gradle test
```

To generate a coverage report:
```
gradle jacocoTestCoverageVerification
```


And
```
gradle jacocoTestReport
```
The coverage report is generated in: build/reports/jacoco/test/html, which does not get pushed to the repo. Open index.html in your browser to see the report. 

## Static analysis
```
gradle checkStyleMain
gradle checkStyleTest
gradle pmdMain
gradle pmdTest
```

## Authors
* **Dimitar Barantiev** - D.A.Barantiev@student.tudelft.nl
* **Jorian Faber** - J.E.C.Faber@student.tudelft.nl
* **Nina Immig** - N.M.Immig@student.tudelft.nl
* **Ivan Nestorov** - I.N.Nestorov@student.tudelft.nl
* **Waded Oudhuis** - W.J.Oudhuis@student.tudelft.nl
* **Simcha Vos** - A.S.J.Vos@student.tudelft.nl

## Notes
- If you change the name of the repo to something other than template, you should also edit the build.gradle file.
- You can add issue and merge request templates in the .gitlab folder on your repo. 