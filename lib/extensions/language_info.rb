LanguageList::LanguageInfo.class_eval do
  def iso_and_name
    '(' + iso_639_1 + ') ' + name
  end
end
