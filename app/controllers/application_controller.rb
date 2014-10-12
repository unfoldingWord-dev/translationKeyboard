class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception


  helper_method :get_all_languages
  def get_all_languages
    Keyboard.all
  end

  helper_method :get_all_languages_distinct
  def get_all_languages_distinct
    Keyboard.select(:iso_language).order(:iso_language).uniq
  end

  helper_method :languages_count
  def languages_count
    Keyboard.all.count
  end

  helper_method :languages_per_column
  def languages_per_column
    Keyboard.all.count / 3
  end

  helper_method :new_keyboard
  def new_keyboard
    Keyboard.new
  end

end
