# Bolt: A Framework for Orchestration of MapReduce Tasks Using Distributed Hash Tables

Developed a framework, *Bolt*, for executing MapReduce jobs
in a structured peer-to-peer system (P2P) based on distributed
hash tables (DHTs). Bolt is decentralized without the need for
an application master per-job and relies only on the DHT to
manage the execution of jobs. Tasks belonging to different jobs
are assigned such that the framework can achieve both good
load balancing and high scalability without the need for a global
knowledge base. 

## Features
- Ensuring the effective distribution of datasets. Since the files being processed are large, it is important to ensure that the distribution of a file facilitates concurrent processing. The distribution scheme must also ensures that the storage loads are effectively dispersed, even in cases where the underlying files are vastly different sizes.
- Decentralized orchestration of Map and Reduce tasks.
DHTs are highly decentralized, orchestration frameworks designed over DHTs must be decentralized as well. Furthermore, centralized components introduce hotspots and points of irrecoverable failures.
- Ensuring fast completion times. The choice of the underlying DHT-based storage subsystem that is completely decentralized should not introduce orchestration overheads that preclude timely completion of jobs.
- Minimizing garbage collection overhead by reusing of objects instead of creating new ones for new data.
