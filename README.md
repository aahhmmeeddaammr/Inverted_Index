Inverted Index Implementation

Overview

This project implements an Inverted Index in Java, a fundamental data structure for information retrieval systems. The inverted index allows efficient searching of documents by mapping terms to the documents in which they appear.

Features

Dictionary-based indexing: Uses a DictEntry class to store term frequency and document frequency.

Posting list with linked structure: Each term maintains a linked list of Posting objects, each containing a document ID and term frequency.

Source Record Storage: SourceRecord class stores document metadata such as URL, title, and text.

Index Building & Storage: Index5 class processes documents, builds the index, and stores it.

Boolean Model Search: Implements a basic Boolean retrieval model.

Command-Line Search Interface: Users can input search queries to retrieve matching documents.

Project Structure

|-- invertedIndex/
| |-- DictEntry.java # Dictionary entry containing term frequency and postings list
| |-- Posting.java # Posting list node representing document occurrences
| |-- SourceRecord.java # Stores metadata for each document
| |-- Test.java # Main class to build the index and perform searches
| |-- Index5.java # (Assumed) Class responsible for managing the index

Class Descriptions

1. DictEntry

Represents an entry in the dictionary:

doc_freq: Number of documents containing the term.

term_freq: Number of times the term appears in the entire collection.

pList: Head of the posting list (linked list of documents containing the term).

2. Posting

Represents a node in the posting list:

docId: The document ID where the term appears.

dtf: The frequency of the term in the document.

next: Pointer to the next posting in the list.

3. SourceRecord

Stores metadata about documents:

fid: File ID.

URL: Document URL.

title: Title of the document.

text: Content of the document.

norm: Normalized weight (default 0.0).

length: Document length.

4. Test

Main entry point to test the index:

Builds the index from a directory of documents.

Prints the dictionary.

Performs Boolean model queries.

Provides an interactive search prompt.

Usage

Building the Index

Place your collection of documents in the specified directory.

Modify the files variable in Test.java to point to your directory.

Run Test.java to build and store the index.

Searching the Index

After indexing, run Test.java.

Enter a search query when prompted.

View the retrieved results based on the Boolean model.

Requirements

Java Development Kit (JDK) 8+

Future Improvements

Implement ranked retrieval with TF-IDF.

Support phrase queries.

Improve index storage efficiency using serialization or databases.

Optimize query processing for faster retrieval.

Author

Developed by Ahmed Amr, Khalid Mohamed, Youssef Mohamed, Malak Ahmed, menna Hesham, and osama Yasser.
