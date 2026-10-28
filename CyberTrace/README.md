
# CyberTrace — Security Log Analyzer

CyberTrace is a Java Swing desktop application that analyzes
server log files and flags repeated failed login attempts.

## Features

- Import .log and .txt files
- Display parsed log events in a table
- Count total log entries and failed logins
- Group failed login attempts by IP address
- Flag IP addresses reaching a configurable threshold
- Display an analysis report

## Requirements

- JDK 17 or newer
- No external dependencies

## Run

From the parent repository directory:

```bash
javac CyberTrace/CyberTrace.java
java -cp CyberTrace CyberTrace
```

On Windows PowerShell, backslashes can also be used
in the javac file path.

## Log format

Each supported line follows this format:

DATE TIME LEVEL IP EVENT

Example:

2026-10-28 09:01:12 WARN 203.0.113.45 Login failed

## Limitations

- The parser expects the format shown above.
- The alert threshold is currently three failed logins.
- Alerts are indicators for investigation, not proof
  of malicious activity.
- This version does not persist data or export CSV reports.

## Future improvements

- CSV export
- Configurable alert thresholds
- Filtering and sorting
- Background analysis for large files
- Unit tests

## Disclaimer

Use CyberTrace for authorized defensive analysis and
synthetic or otherwise authorized log data.
