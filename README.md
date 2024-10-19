# Numerical Methods Lecture

This repository contains the material created for and during the lecture *Numerical Methods for Mathematical Finance*.

In addition it contains code used in the exercise, e.g. interfaces which are to be implemented.

## Versions

The lecture is held regularly with a varying selection of topics. The repository evolves and improves.
If you like to have the version associated with a specific year you can switch to the corresponding
git branch. The main branch should be up to date with the latest version of the lecture.

In the following an incomplete list of code used in the different chapters of the lecture.

---


## Computer Arithmetic

### Sessions

See the package `info.quantlab.numericalmethods.lecture.computerarithmetics`

- `IntegerArithmeticExperiment` - Elementary things related to integer arithmetic: Integer is an equivalence class.
- `FloatingPointNumbersExperiment` - Elementary things related to floating point numbers: Understanding their representation.
- `FloatingPointArithmeticExperiment` - Elementary things related to rounding.
- `QuadraticEquationExperiment` - An example for loss of significance: solving a quadratic equation.
- `SummationExperiment` - An example for loss of significance: summation.

### Assignments

We provide small coding assignments related to the lecture. The task is described in the README.md of the corresponding repository. Each repository contains a Maven project including unit test that will perform an "Autograding" of the assignment.

#### Loss of Significance (quadratic equation)

https://github.com/qntlb/numerical-methods-quadraticequation-exercise

#### Summation (Kahan summation)

https://github.com/qntlb/numerical-methods-summation-exercise

---


## Monte-Carlo Simulation

### Sessions

#### Introduction

The graph from the motivation session is generated by the class RunningAverageOfIndicator

```
net.finmath.lecture.numericalmethods.montecarlo.RunningAverageOfIndicator
```

#### Generating Vectors from Sequences

An example plotting 2D samples from 1D sequence:

```
info.quantlab.numericalmethods.lecture.randomnumbers.plots.RandomVectorPlot
```


---

## Monte-Carlo Integration

### Sessions

In the session on Monte-Carlo integration a one-dimensional integrator is implemented, using the
a) Monte-Carlo integration and b) the Simpson's rule.

The implementation can be found in the package

```
info.quantlab.numericalmethods.lecture.montecarlo.integration1d
```


The integrators are then tested in a unit test (so this time, there is no "Experiment" with a main() method). The tests can be found in src/test/java in the package

```
info.quantlab.numericalmethods.lecture.montecarlo.integration1d
```

#### Monte-Carlo Approximation of Pi

The code for the example at the end of the section can be found in the [finmath-experiments repository](https://github.com/finmath/finmath-experiments) at [https://github.com/finmath/finmath-experiments](https://github.com/finmath/finmath-experiments) in the class `MonteCarloIntegrationExperiment` in the package `net.finmath.experiments.montecarlo`.


#### Java Streams

The code of the excursus on Java streams can be found in 

```
info.quantlab.numericalmethods.lecture.streams.JavaStreamsExperiments
```

### Assignments

The interfaces related to the coding assignments for implementing a general integrator are in the package

```
info.quantlab.numericalmethods.lecture.montecarlo.integration
```

- `Integrand`
- `IntegrationDomain`
- `Integrator`
- `Monte-CarloIntegratorFactory`


---

<script type="text/javascript"
  src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_CHTML">
</script>
<script type="text/x-mathjax-config">
  MathJax.Hub.Config({
    tex2jax: {
      inlineMath: [['$','$'], ['\\(','\\)']],
      processEscapes: true},
      jax: ["input/TeX","input/MathML","input/AsciiMath","output/CommonHTML"],
      extensions: ["tex2jax.js","mml2jax.js","asciimath2jax.js","MathMenu.js","MathZoom.js","AssistiveMML.js", "[Contrib]/a11y/accessibility-menu.js"],
      TeX: {
      extensions: ["AMSmath.js","AMSsymbols.js","noErrors.js","noUndefined.js"],
      equationNumbers: {
      autoNumber: "AMS"
      }
    }
  });
</script>
