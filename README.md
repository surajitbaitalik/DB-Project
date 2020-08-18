# DB-Project
The goal of this project is to implement a (very) rudimentary database engine that is based on a simplified
file-per-table variation on the SQLite file format, which we call DavisBase. Your implementation should
operate entirely from the command line and possibly API calls (no GUI).
Like MySQL's InnoDB data engine (SDL), your program will use file-per-table approach to physical
storage. Each database table will be physically stored as a separate single file. Each table file will be
subdivided into logical sections of fixed equal size call pages. Therefore, each table file size will be exact
increments of the global page_size attribute, i.e. all data files must share the same page_size attribute.
You may make page_size be a configurable attribute, but your implementation must capable of
supporting a page size of 512 Bytes. The test scenarios for grading will be based on a page_size of 512B.
Once a database is initialized, your are not required to support a reformat change to its page_size (but you
may implement such a feature if you choose).
