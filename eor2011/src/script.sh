#!/bin/bash
# Run multiple instance of StressTest.java
# Usage: java StressTest <#clients> [<#objects>] [read|write|both]
# 

for i in {1..$0}
do
    java -cp /home/pbonaud/workspace/eor2011/bin /home/pbonaud/workspace/eor2011/bin/StressTest 10 10 both
done
