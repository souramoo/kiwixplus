#include <string.h>
#include <string>
#include <jni.h>
#include <xapian.h>

using namespace std;

const char* executeQuery(const char* dbLoc, const char* qu) try {
    Xapian::Database db(dbLoc);

    // Start an enquire session.
    Xapian::Enquire enquire(db);

    string query_string(qu);
    string reply("");

    // Parse the query string to produce a Xapian::Query object.
    Xapian::QueryParser qp;
    Xapian::Stem stemmer("english");
    qp.set_stemmer(stemmer);
    qp.set_database(db);
    qp.set_stemming_strategy(Xapian::QueryParser::STEM_SOME);
    Xapian::Query query = qp.parse_query(query_string);

    // Find the top 10 results for the query.
    enquire.set_query(query);
    Xapian::MSet matches = enquire.get_mset(0, 10);

    for (Xapian::MSetIterator i = matches.begin(); i != matches.end(); ++i) {
        reply += i.get_document().get_data();
        reply += "\n";
    }
    return reply.c_str();
}  catch (const Xapian::Error &e) {
    return e.get_description().c_str();
}

extern "C" {
    JNIEXPORT jstring JNICALL
    Java_com_moosd_kiwixplus_IndexedSearch_query(JNIEnv *env,
                                                     jclass thiz, jstring db, jstring qu)
    {
        const char* d = env->GetStringUTFChars( db , 0 ) ;
        const char* q = env->GetStringUTFChars( qu , 0 ) ;
        const char* result = executeQuery(d, q);
        return env->NewStringUTF(result);
    }
}
