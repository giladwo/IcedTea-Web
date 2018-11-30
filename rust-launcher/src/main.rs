mod hardcoded_paths;
mod property_from_file;
mod os_access;
mod dirs_paths_helper;
mod property_from_files_resolver;
mod utils;
mod property;

use std::string::String;
use std::fmt::Write;
use os_access::Os;
use std::env;

fn is_debug_on() -> bool{
    for s in env::args() {
        //this can go wrong with case like -jnlp file-verbose or -html file-verbose
        //but it is really unlikely case as those are ususally .jnlp or .html suffixed
        if s.ends_with("-verbose") {
            return true;
        }
    }
    let os = os_access::Linux::new(false);
    return property_from_files_resolver::try_main_verbose_from_properties(&os);
}

fn main() {
    //TODO verbose will be populated by also from deployment properties
    let os = os_access::Linux::new(is_debug_on());
    let java_dir: std::path::PathBuf;
    let mut info1 = String::new();
    write!(&mut info1, "itw-rust-debug: trying jdk over properties ({})", property_from_file::JRE_PROPERTY_NAME).expect("unwrap failed");
    os.log(&info1);
    match property_from_files_resolver::try_jdk_from_properties(&os) {
        Some(path) => {
            java_dir = std::path::PathBuf::from(path);
            os.log("itw-rust-debug: found and using");
        }
        None => {
            os.log("itw-rust-debug: nothing");
            os.log("itw-rust-debug: trying jdk JAVA_HOME");
            match env::var("JAVA_HOME") {
                Ok(war) => {
                    java_dir = std::path::PathBuf::from(war);
                    os.log("itw-rust-debug: found and using");
                }
                Err(_e) => {
                    os.log("itw-rust-debug: nothing");
                    os.log("itw-rust-debug: trying jdk from registry");
                    match os.get_registry_jdk() {
                        Some(path) => {
                            java_dir = path;
                            os.log("itw-rust-debug: found and using");
                        }
                        None => {
                            os.log("itw-rust-debug: nothing");
                            os.log("itw-rust-debug: failing down to hardcoded");
                            java_dir = std::path::PathBuf::from(hardcoded_paths::get_jre());
                        }
                    }
                }
            }
        }
    }
    let mut info2 = String::new();
    write!(&mut info2, "selected jre: {}", java_dir.display()).expect("unwrap failed");
    os.info(&info2);
    let mut child = os.spawn_java_process(&java_dir, &(env::args().skip(1).collect()));
    let ecode = child.wait().expect("failed to wait on child");
    let code = ecode.code().expect("code should be always here");
    std::process::exit(code)
}


