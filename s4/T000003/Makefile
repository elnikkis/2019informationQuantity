JFLAGS = -classpath "../.."

all:
	@echo "Usage: make [ Hello | Frequencer | InformationEstimator | TestCase ]"

run:
	javac Hello.java -classpath "../.."
	javac Frequencer.java -classpath "../.."
	javac InformationEstimator.java -classpath "../.."
	javac TestCase.java -classpath "../.."
#	java -classpath "../.." s4.T000003/Hello
#	java -classpath "../.." s4.T000003/Frequencer
#	java -classpath "../.." s4.T000003/InformationEstimator
	java -classpath "../.." s4.T000003/TestCase


.PNOHY: Hello Frequencer InformationEstimator TestCase

Hello: Hello.class
	java $(JFLAGS) s4.T000003/Hello

Frequencer: Frequencer.class
	java $(JFLAGS) s4.T000003/Frequencer

InformationEstimator: InformationEstimator.class
	java $(JFLAGS) s4.T000003/InformationEstimator

TestCase: TestCase.class
	java $(JFLAGS) s4.T000003/TestCase


Hello.class: Hello.java
	javac $(JFLAGS) Hello.java

Frequencer.class: Frequencer.java
	javac $(JFLAGS) Frequencer.java

InformationEstimator.class: InformationEstimator.java
	javac $(JFLAGS) InformationEstimator.java

TestCase.class: TestCase.java
	javac $(JFLAGS) TestCase.java
