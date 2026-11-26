# Car Showroom Management System

Advanced console-based mini project developed using Core Java.

## Features
- Admin and sales executive login
- Car inventory: add, update, remove, adjust stock, search, and low-stock alerts
- Customer registration and customer purchase/service history
- Test-drive booking with duplicate slot checking and booking status management
- Vehicle sales with discount validation, GST calculation, payment mode, and invoice generation
- Finance simulation with down payment, annual interest rate, tenure, and EMI calculation
- After-sales service booking and service status updates
- Sales reports: business summary, sales by model, top-selling models, and payment-mode summary
- Multithreaded independent report generation using ExecutorService
- Collections: LinkedHashMap, ArrayList, HashMap, TreeMap, PriorityQueue, Comparator, and Streams
- File handling: records persist in text files after closing the application
- Input validation and synchronized sale/stock update

## Requirements
- JDK 8 or later
- Windows, macOS, or Linux
- Any Java IDE or terminal

## Compile and run
From the project folder:

```bash
javac *.java
java Main
```

The ZIP contains Java source files and this README only. No compiled `.class` files are included.

## Demo credentials
Admin:
- Username: `admin`
- Password: `Admin@123`

Sales executive:
- Username: `sales`
- Password: `Sales@123`

These are demonstration credentials stored in source code. Change them before any real deployment. This project is an educational simulation and does not implement production-grade authentication, payment processing, or statutory tax invoicing.

## Default sample inventory
On first run, the system creates four sample car models if there is no saved car inventory:
- CAR101 – Maruti Baleno
- CAR102 – Hyundai Creta
- CAR103 – Tata Nexon EV
- CAR104 – Toyota Innova

## Data files created at runtime
The program creates `data/` and `invoices/` folders in the current working directory.

- `data/cars.txt`
- `data/customers.txt`
- `data/sales.txt`
- `data/testdrives.txt`
- `data/services.txt`
- `invoices/INVxxxx.txt` for generated invoices

Do not manually edit the data files while the application is running.

## Important implementation notes
- The GST percentage is set to 18% as a configurable classroom example in `CarShowroomSystem.java`; confirm applicable tax rules before real use.
- Finance mode calculates an illustrative EMI and does not contact a bank or lender.
- The multithreading demonstration generates independent business reports concurrently. Stock deduction and sale creation are synchronized to avoid simultaneous sales overselling the same stock in a shared runtime.
- The app uses simple demonstration credentials and text-file persistence, not a database.
