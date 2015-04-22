class MigrateKeyboards < ActiveRecord::Migration
  def change

    all_keyboards = Keyboard.all

    all_keyboards.each do |a_keyboard|

      Keyboard.move_to_new_structure(a_keyboard.id)

    end

  end
end
