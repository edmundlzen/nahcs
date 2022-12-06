import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(requireContext(), this, hour, minute, false)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val twelveHour = hourOfDay % 12
        val time =
            String.format(
                "%02d:%02d %s",
                if (twelveHour == 0) 12 else twelveHour,
                minute,
                if (hourOfDay < 12) "AM" else "PM"
            )
        val bundle = Bundle()
        bundle.putString("time", time)
        parentFragmentManager.setFragmentResult("timePicker", bundle)
    }
}