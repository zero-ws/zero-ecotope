---
description: Comprehensive guide for Zero Framework DBE (Database Engine) QR Query Syntax. Defines the JSON structure for QQuery (Request) and QTree (Criteria) used in database access.
globs: *.json, src/main/java/**/*.java
alwaysApply: true
---

# Zero Framework DBE Query Syntax (QR)

This rule defines the **QR Query Syntax** used by the Zero Framework DBE (Database Engine).
DBE provides an implementation-agnostic way to access databases using a standardized JSON structure.

## 1. Request Structure (QQuery)

The standard request DTO in Zero Framework can be constructed using `QQuery.of()`.
It consists of four main components used to instruct the database engine.

```json
{
    "criteria": {
        "comment": "Query conditions (QTree), corresponds to WHERE clause"
    },
    "pager": {
        "page": 1,
        "size": 10
    },
    "sorter": [
        "field1,ASC",
        "field2,DESC"
    ],
    "projection": [
        "field1", 
        "field2"
    ]
}
```

### Component Definitions
* **criteria (JObject)**: Corresponds to `QTree`. This is the core query condition / syntax tree (WHERE clause).
* **pager (JObject)**: Pagination parameters (Page number starts from 1).
* **sorter (JArray)**: Sorting parameters (Field + Direction).
* **projection (JArray)**: Column filtering (SELECT fields). If empty, selects all columns.

| Attribute      | Type      | Description                            |
| :------------- | :-------- | :------------------------------------- |
| **criteria**   | `JObject` | Query conditions / Syntax Tree (QTree) |
| **pager**      | `JObject` | Pagination parameters                  |
| **sorter**     | `JArray`  | Sorting parameters                     |
| **projection** | `JArray`  | Column filtering / Select fields       |

---

## 2. Criteria Syntax (QTree)

The `criteria` node follows the **QTree** syntax. It is a nested JSON object composed of three types of nodes:

1.  **Direct Node**: Defines a condition on a specific field.
    * Format: `"field,op": value`
    * `field`: The attribute/column name.
    * `op`: The operator (e.g., `=`, `<`, `c`).
    * `value`: The target value.
2.  **Nested Node**: Used for grouping conditions or sub-queries.
    * Format: `"column": {}` or `"$any": {}`
3.  **Connector Node**: Defines the logical relationship (AND/OR).
    * Format: `"": true` (AND) or `"": false` (OR).
    * The key is an empty string `""`.
    * This key has no business meaning; it serves only as a logical placeholder.

---

## 3. Operators (The 'op' Suffix)

In a Direct Node `"field,op"`, the suffix determines the comparison logic.
If no operator is provided (e.g., `"name": "Value"`), it defaults to **Equal (`=`)**.

| Operator | Format Example         | Meaning               | Equivalent SQL          |
| :------- | :--------------------- | :-------------------- | :---------------------- |
| `<`      | `"age,<": 20`          | Less than             | `AGE < 20`              |
| `<=`     | `"age,<=": 20`         | Less than or equal    | `AGE <= 20`             |
| `>`      | `"age,>": 20`          | Greater than          | `AGE > 20`              |
| `>=`     | `"age,>=": 20`         | Greater than or equal | `AGE >= 20`             |
| `=`      | `"name,=": "A"`        | Equal (Default)       | `NAME = 'A'`            |
| `<>`     | `"name,<>": "A"`       | Not equal             | `NAME <> 'A'`           |
| `!n`     | `"name,!n": "any"`     | Is Not Null           | `NAME IS NOT NULL`      |
| `n`      | `"name,n": "any"`      | Is Null               | `NAME IS NULL`          |
| `i`      | `"name,i": ["A","B"]`  | In List               | `NAME IN ('A','B')`     |
| `!i`     | `"name,!i": ["A","B"]` | Not In List           | `NAME NOT IN ('A','B')` |
| `s`      | `"name,s": "Z"`        | Starts With           | `NAME LIKE 'Z%'`        |
| `e`      | `"name,e": "Z"`        | Ends With             | `NAME LIKE '%Z'`        |
| `c`      | `"name,c": "Z"`        | Contains              | `NAME LIKE '%Z%'`       |

---

## 4. Logic & Connectors

To combine multiple conditions, use the **Connector Node** (Empty Key `""`).

* **AND Logic (Default)**: `"": true`. If omitted, DBE defaults to AND.
* **OR Logic**: `"": false`.

### Complex Logic (Nesting)
To mix AND and OR logic (e.g., `(A AND B) OR (C AND D)`), use nested objects.
* You can use arbitrary keys starting with `$` (e.g., `"$0"`, `"$1"`) to create nested groups that don't correspond to actual columns but act as logical brackets.

---

## 5. Examples & Equivalents

Understanding how JSON translates to SQL is critical.

**Scenario 1: Simple AND**
Query: `Name is 'Lang' AND Email starts with 'lang.yu'`

```json
{
  "name": "Lang",
  "email,s": "lang.yu"
}
```

**Scenario 2: Simple OR**
Query: `Name is 'Lang' OR Email starts with 'lang.yu'`

```json
{
  "": false,
  "name": "Lang",
  "email,s": "lang.yu"
}
```

**Scenario 3: Mixed Logic (Nested)**
Query: `Name is 'Lang' OR (Email starts with 'lang.yu' AND Age >= 18 AND Age <= 60)`

```json
{
  "": false,
  "name": "Lang",
  "$1": {
    "": true,
    "email,s": "lang.yu",
    "age,>=": 18,
    "age,<=": 60
  }
}
```

**Scenario 4: Same Field OR**
Query: `Name contains 'lang' OR Name contains 'yu'`

```json
{
  "name,c": "lang",
  "": false,
  "$0": {
    "name,c": "yu"
  }
}
```

---

## 6. AI Generation Guidelines

When generating QTree JSON:
1.  **Identify Operator**: Always map natural language to the correct `op` suffix (e.g., "contains" -> `,c`, "start with" -> `,s`).
2.  **Handle Nulls**: Use `,n` for IS NULL and `,!n` for IS NOT NULL. The value is usually ignored or set to "any".
3.  **Boolean Logic**: Explicitly use `"": false` for OR conditions.
4.  **Structure**: Place pagination and sorting outside the `criteria` object.