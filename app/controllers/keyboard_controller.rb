class KeyboardController < ApplicationController

  def index
    get_all_keyboards
    @keyboard = Keyboard.first
    @selected_keyboard_variant = @keyboard.keyboard_variants.first
    set_key_positions
  end

  def get_keyboard_variant
    get_all_keyboards
    @selected_keyboard_variant = KeyboardVariant.find(params[:keyboard_variant_id])
    set_key_positions
    render "keyboard/index"
  end

  def get_all_keyboards
    @all_keyboards = Keyboard.all
  end

  def set_key_positions
    @key_positions = KeyPosition.where(keyboard_variant: @selected_keyboard_variant).order(:row_index, column_index: :asc)
  end

end
