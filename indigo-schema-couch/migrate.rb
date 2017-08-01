require "couchrest"
require 'optparse'

class Options

  def self.parse(args) 
    options = OpenStruct.new
    opt_parser = OptionParser.new do |opts| 
      opts.banner = "Usage: migrate.rb [options]"
      opts.on('-s', '--source HOST', 'Source HOST endpoint') { |v| options.source = v }
      opts.on('-i', '--source-db NAME', 'Source database NAME') { |v| options.source_db = v }
      opts.on('-t', '--target HOST', 'Target HOST endpoint') { |v| options.target = v }
      opts.on('-o', '--target-db NAME', 'Target database NAME') { |v| options.target_db = v }
      if options[:source].nil? then options[:source] = "http://localhost:5984" end
      if options.target.nil? then options.target = "http://localhost:5984" end
    end
    opt_parser.parse!(args)
    options
  end

end

def migrate(options) 
  docs = CouchRest.get(options.source+"/"+ options.source_db+"/_all_docs?include_docs=true")
  db_target = CouchRest.new(options.target).database(options.target_db)
  docs["rows"].each do |row|
    row["doc"].delete("_rev")
    if (!row["id"].start_with?("_")) 
      begin 
        db_target.save_doc(row["doc"]);
      rescue  => exception
        puts "Eror while saving #{row["doc"]}"
        puts exception
      end
    end
  end
end

options = Options.parse(ARGV)
puts options
migrate(options)

