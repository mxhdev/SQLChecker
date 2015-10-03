# SQLChecker

Checks student submissions automatically

## How it works

1. Read the solutions.txt file
	1.1 Also read the connection data and the tag list. Each tag corresponds to one task of the assignment.
2. Read all student submissions
	2.1 Extract the (tag-> submissionSQL) mapping from each student submission
3. Apply each mapping extracted in 2. to the solutions file read in 1. Each tag in the solutions file will 
be replaced by the sql found in the student submission
4. Execute each "replacement" via Fitnesse and DBFit
5. Write the results of 4. to files
	5.1 Write to a csv file ("summary")
	5.2 Write to a log file ("mistakes)

## Output

This section describes the content of the output files. Each file has a unique number in its file name. This is
the date/time of its creation in the following format 

```
yyyyMMddHHmmss
```

Example file names:

* summary_20151003165859.csv
* mistakes_20151003165859.log

### Summary file

The summary file shows the summary of running all the student submissions. Each row corresponds to a student submission, each
column corresponds to a count of correct/wrong/faulty/ignored statements and a status label for each query.
In the following columns, the table shows the result of running each individual query. An example for the content
of a summary file could be:
```
Submission;Right;Wrong;Ignored;Exceptions;Query1 (1a);Query4
```
The first field identifies the submission, the following four fields contain counts which show, how many statements 
were right, wrong, ignored or caused an error.

### Log file
This log contains an entry for each submission. If a submission contains a query which was wrong, ignored or caused an error,
this statement will be written in this file. This consists of the query, which is called, the command, the
expected results and any Exceptions.


