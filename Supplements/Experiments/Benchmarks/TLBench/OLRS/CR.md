// ==version1==
```
1. Add a new item line to order. Preconditions.The order must be in Pending status; the library item must be available (not Hold or Loan). Ensure no duplicate item line (same library item) exists in the order. And modify the library item's status from available to hold only if the library item is a print format book item or disc digital item. Otherwise, keep the status as Available. Return true if item line added, false if validation fails.

2. Remove an item line from an existing order in Pending status. If successful: Return the updated count of item lines in the order. Reset the library item's status to available only if it was previously Hold (for print/disc items). Otherwise, retain its original status. If failed (order is completed): Return -1.

3. Handle an existing order. First, update Order Status to "completed". And then modify the library item's status from hold to loan only if the library item belongs to a print format book item or disc digital item.

4. Calculate the total downloads for a specific Audio digital item. Only consider orders with Completed status. Only count if the digital item's option is Downloadable (not Disc). Return total download count. If no download, return 0.

5. Count the total number of print-format book items in a given order only if the order status is completed. Return 0 if none.
```
// ==end==